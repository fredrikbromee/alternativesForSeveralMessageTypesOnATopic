import com.google.protobuf.Any;
import com.google.protobuf.Message;
import com.example.any.*;
import com.example.*;
import com.google.protobuf.InvalidProtocolBufferException;

import java.util.HashMap;
import java.util.Map;

/**
 * This test shows how to get different message types from an envelope that uses the Any concept
 * to hold messages of different types.
 */
public class UsingTheAnyConceptToDeclareMessageType {

  private final Map<String, Class<? extends Message>> classBuilderMap = new HashMap<>();

  public UsingTheAnyConceptToDeclareMessageType(){
    classBuilderMap.put(AccountTypeEvent.class.getSimpleName(), AccountTypeEvent.class);
    classBuilderMap.put(Marker.class.getSimpleName(), Marker.class);
  }

  @org.junit.Test
  public void testSerializeAndDeserializeAnAccountTypeEventUsingAny() throws InvalidProtocolBufferException {
    // Given an AccountTypeEvent packed in an envelope using an 'Any' field
    AccountTypeEvent event = createEvent();
    TopicEnvelopeUsingAny msg = TopicEnvelopeUsingAny.newBuilder()
            .setHeader(createHeader("XYZ"))
            .setEvent(Any.pack(event))
            .build();

    // When I serialize that event
    byte[] serialized = msg.toByteArray();

    // Then I expect gpb to give me an account type event back when unpacking
    Message message = getMessage(serialized);
    org.junit.Assert.assertEquals("Expected to receive an AccountTypeEvent!",
            AccountTypeEvent.class,
            message.getClass());
  }

  @org.junit.Test
  public void testSerializeAndDeserializeAMarkerEventUsingAny() throws InvalidProtocolBufferException {
    // Given a Marker packed in an envelope using an 'Any' field
    Marker event = Marker.newBuilder().setPointInTime("This marks start of the business day").build();
    TopicEnvelopeUsingAny msg = TopicEnvelopeUsingAny.newBuilder()
            .setHeader(createHeader("ABC"))
            .setEvent(Any.pack(event))
            .build();

    // When I serialize that event
    byte[] serialized = msg.toByteArray();

    // Then I expect gpb to give me a marker message back when unpacking
    Message message = getMessage(serialized);
    org.junit.Assert.assertEquals("Expected to receive a marker message!",
            Marker.class,
            message.getClass());
  }

  private Message getMessage(byte[] serialized) throws InvalidProtocolBufferException {
    TopicEnvelopeUsingAny parsedEnvelope = TopicEnvelopeUsingAny.parseFrom(serialized);
    System.out.println("Parsed message with correlation id " + parsedEnvelope.getHeader().getCorrelationId());
    Any any = parsedEnvelope.getEvent();
    String eventType = getTypeNameFromTypeUrl(any.getTypeUrl());
    System.out.println("Message is an event of type: " + eventType);
    Message event = getMessage(any, eventType);
    System.out.println(event);
    return event;
  }

  private Message getMessage(Any any, String type) throws InvalidProtocolBufferException {
    var msgClass = classBuilderMap.get(type);
    if (msgClass == null) {
      throw new IllegalArgumentException("No Protobuf message found for " + type);
    }
    return any.unpack(msgClass);
  }

  /**
   * Copied from com.google.protobuf.Any#getTypeNameFromTypeUrl(java.lang.String)
   */
  private static String getTypeNameFromTypeUrl(String typeUrl) {
    int pos = typeUrl.lastIndexOf('/');
    return pos == -1 ? "" : typeUrl.substring(pos + 1);
  }

  private Header createHeader(String correlationId) {
    return Header.newBuilder().setCorrelationId(correlationId).build();
  }

  private AccountTypeEvent createEvent() {
    AccountTypeEntity entity = AccountTypeEntity.newBuilder()
            .setName("Fredriks account type")
            .setId(12).build();
    return AccountTypeEvent.newBuilder().setEntity(entity).build();
  }
}
