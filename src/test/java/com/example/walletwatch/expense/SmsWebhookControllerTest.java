package com.example.walletwatch.expense;

import com.example.walletwatch.expense.controller.SmsWebhookController;
import com.example.walletwatch.expense.model.MultipleSmsRequest;
import com.example.walletwatch.expense.model.SmsRequest;
import com.example.walletwatch.expense.model.TransactionDetails;
import com.example.walletwatch.expense.service.MessageParsingService;
import com.example.walletwatch.expense.repository.TransactionDetailsRepository;
import com.example.walletwatch.expense.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SmsWebhookControllerTest {

    @Mock
    private MessageParsingService messageParsingService;

    @Mock
    private TransactionDetailsRepository transactionDetailsRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SmsWebhookController controller;

    @Test
    void testHandleIncomingSms() {
        SmsRequest request = new SmsRequest();
        request.setBody("Test message");
        request.setUserId(1L);
        TransactionDetails details = new TransactionDetails();
        when(messageParsingService.parseMessage(anyString(), anyLong())).thenReturn(details);

        ResponseEntity<?> response = controller.handleIncomingSms(request);

        assertEquals(details, response.getBody());
        verify(messageParsingService).parseMessage(anyString(), anyLong());
    }

    @Test
    void testHandleIncomingMultipleSms() {
        List<SmsRequest> smsRequests = new ArrayList<>();
        SmsRequest request1 = new SmsRequest();
        request1.setBody("Test message 1");
        request1.setUserId(1L);
        smsRequests.add(request1);

        SmsRequest request2 = new SmsRequest();
        request2.setBody("Test message 2");
        request2.setUserId(1L);
        smsRequests.add(request2);

        MultipleSmsRequest multipleSmsRequest = new MultipleSmsRequest();
        multipleSmsRequest.setTransactionDetails(smsRequests);
        multipleSmsRequest.setUserId(1L);

        when(messageParsingService.parseMessage(anyString(), anyLong())).thenReturn(new TransactionDetails());

        ResponseEntity<String> response = controller.handleIncomingMultipleSms(multipleSmsRequest);

        assertEquals("", response.getBody());
        verify(messageParsingService, times(2)).parseMessage(anyString(), anyLong());
    }
}
