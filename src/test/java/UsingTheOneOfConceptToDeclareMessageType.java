import com.example.AccountTypeEntity;
import com.example.AccountTypeEvent;
import com.example.Header;
import com.example.Marker;
import com.example.oneof.TopicEnvelopeUsingOneOf;
import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

import java.util.HashMap;
import java.util.Map;

/**
 * This test shows how to get different message types from an envelope that uses the OneOf concept
 * to hold messages of different types.
 */
public class UsingTheOneOfConceptToDeclareMessageType {


  @org.junit.Test
  public void testSerializeAndDeserializeAnAccountTypeEventUsingAny() throws InvalidProtocolBufferException {
    // Given an AccountTypeEvent packed in an envelope using an 'Any' field
    AccountTypeEvent event = createEvent();
    TopicEnvelopeUsingOneOf msg = TopicEnvelopeUsingOneOf.newBuilder()
            .setHeader(createHeader("XYZ"))
            .setAccountType(event)
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
    Marker event = Marker.newBuilder().setPointInTime("SOD baby").build();
    TopicEnvelopeUsingOneOf msg = TopicEnvelopeUsingOneOf.newBuilder()
            .setHeader(createHeader("ABC"))
            .setMarker(event)
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
    TopicEnvelopeUsingOneOf parsedEnvelope = TopicEnvelopeUsingOneOf.parseFrom(serialized);
    System.out.println("Parsed message with correlation id " + parsedEnvelope.getHeader().getCorrelationId());
    switch (parsedEnvelope.getEventCase()) {
      case ACCOUNTTYPE -> {
        System.out.println(parsedEnvelope.getAccountType());
        return parsedEnvelope.getAccountType();
      }
      case MARKER -> {
        System.out.println(parsedEnvelope.getMarker());
        return parsedEnvelope.getMarker();
      }
      default ->  throw new RuntimeException("the event is required by convention");
    }
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
