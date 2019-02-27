package de.hhu.propra.sharingplatform.service.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hhu.propra.sharingplatform.dao.contractdao.BorrowContractRepo;
import de.hhu.propra.sharingplatform.model.Offer;
import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.model.contracts.BorrowContract;
import de.hhu.propra.sharingplatform.model.items.ItemRental;
import de.hhu.propra.sharingplatform.service.payment.PaymentService;
import de.hhu.propra.sharingplatform.service.payment.ProPayApi;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.server.ResponseStatusException;


public class OfferValidatorTest {

    private User owner;
    private User borrower;
    private ItemRental itemRental;
    private LocalDateTime start;
    private LocalDateTime end;

    @MockBean
    private ProPayApi proPayApi;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private BorrowContractRepo borrowContractRepo;

    public void alternativeSetUpTests() {
        owner = new User();
        borrower = new User();
        itemRental = new ItemRental(owner);
        itemRental.setBail(234);

        start = LocalDateTime.now().plusDays(3);
        end = start.plusDays(1);

        BorrowContract contractOne = new BorrowContract(
            new Offer(itemRental, borrower, start, end));

        LocalDateTime newStart = end.plusDays(3);
        LocalDateTime newEnd = start.plusDays(4);
        BorrowContract contractTwo =
            new BorrowContract(new Offer(itemRental, borrower, newStart, newEnd));

        List<BorrowContract> contractList = new ArrayList<>();
        contractList.add(contractOne);
        contractList.add(contractTwo);

        borrowContractRepo = mock(BorrowContractRepo.class);
        when(borrowContractRepo.findAllByItem(itemRental)).thenReturn(contractList);
        when(borrowContractRepo.findAllByItemAndFinishedIsFalse(itemRental))
            .thenReturn(contractList);
    }


    @Before
    public void setUpTests() {
        owner = new User();
        borrower = new User();
        itemRental = new ItemRental(owner);
        itemRental.setBail(234);

        start = LocalDateTime.now();
        end = LocalDateTime.now().plusDays(3);
    }

    @Test
    public void periodIsNotAvailable() {
        alternativeSetUpTests();
        boolean thrown = false;

        LocalDateTime testStart = LocalDateTime.now().plusDays(6);
        LocalDateTime testEnd = start.plusDays(9);

        try {

            OfferValidator.periodIsAvailable(borrowContractRepo, itemRental, testStart, testEnd);
        } catch (ResponseStatusException responseException) {
            thrown = true;
            assertEquals("400 BAD_REQUEST \"Invalid period\"",
                responseException.getMessage());
        }
        assertTrue(thrown);
    }

    @Test
    public void periodIsAfterAvailable() {
        alternativeSetUpTests();
        boolean thrown = false;

        start = LocalDateTime.now().plusDays(14);
        end = start.plusDays(2);

        try {
            OfferValidator.periodIsAvailable(borrowContractRepo, itemRental, start, end);
        } catch (ResponseStatusException responseException) {
            thrown = true;
        }
        assertFalse(thrown);
    }


    @Test
    public void periodIsBeforeAvailable() {
        alternativeSetUpTests();
        boolean thrown = false;

        start = LocalDateTime.now();
        end = start.plusDays(2);

        try {
            OfferValidator.periodIsAvailable(borrowContractRepo, itemRental, start, end);
        } catch (ResponseStatusException responseException) {
            thrown = true;
        }
        assertFalse(thrown);
    }


    /*
    // alte tests
    @Test
    public void validateEndIsBeforeStart() {
        LocalDateTime wrongEnd = start.minusDays(1);
        boolean thrown = false;

        paymentService = mock(PaymentService.class);
        when(paymentService.calculateTotalPrice(itemRental, start, end)).thenReturn(120);

        try {
            OfferValidator.validate(itemRental, borrower, start, wrongEnd, paymentService,
            apiService);
        } catch (ResponseStatusException responseException) {
            thrown = true;
            assertEquals("400 BAD_REQUEST \"End date needs to be after Start date\"",
                responseException.getMessage());
        }
        assertTrue(thrown);
    }
*/
    /*
    @Ignore
    @Test
    public void validateEndSameAsStart() {
        boolean thrown = false;

        paymentService = mock(PaymentService.class);
        when(paymentService.calculateTotalPrice(itemRental, start, start)).thenReturn(120);

        try {
            OfferValidator.validate(itemRental, borrower, start, start, paymentService, apiService);
        } catch (ResponseStatusException responseException) {
            thrown = true;
            assertEquals("400 BAD_REQUEST \"End date needs to be after Start date\"",
                responseException.getMessage());
        }
        assertTrue(thrown);
    }
    */

    /*
    @Test
    public void validateIsSolvent() {
        boolean thrown = false;

        paymentService = mock(PaymentService.class);
        apiService = mock(ProPayNetworkInterface.class);
        when(paymentService.calculateTotalPrice(itemRental, start, end)).thenReturn(120);
        when(apiService.isSolvent(eq(borrower), anyInt())).thenReturn(false);

        try {
            OfferValidator.validate(itemRental, borrower, start, end, paymentService, apiService);
        } catch (ResponseStatusException responseException) {
            thrown = true;
            assertEquals("400 BAD_REQUEST \"Not enough money\"",
                responseException.getMessage());
        }
        assertTrue(thrown);
    }
*/
    /*
    @Test
    public void validateIsBanned() {
        boolean thrown = false;

        borrower.setBan(true);

        paymentService = mock(PaymentService.class);
        apiService = mock(ProPayNetworkInterface.class);
        when(paymentService.calculateTotalPrice(itemRental, start, end)).thenReturn(120);
        when(apiService.isSolvent(eq(borrower), anyInt())).thenReturn(true);

        try {
            OfferValidator.validate(itemRental, borrower, start, end, paymentService, apiService);
        } catch (ResponseStatusException responseException) {
            thrown = true;
            assertEquals("400 BAD_REQUEST \"Account currently suspended\"",
                responseException.getMessage());
        }
        assertTrue(thrown);
    }
*/
    /*
    @Test
    public void validateValidInput() {
        boolean thrown = false;

        paymentService = mock(PaymentService.class);
        apiService = mock(ProPayNetworkInterface.class);
        when(paymentService.calculateTotalPrice(itemRental, start, end)).thenReturn(234);
        when(apiService.isSolvent(eq(borrower), anyInt())).thenReturn(true);

        try {
            OfferValidator.validate(itemRental, borrower, start, end, paymentService, apiService);
        } catch (ResponseStatusException responseException) {
            thrown = true;
        }
        assertFalse(thrown);
    }
*/
}