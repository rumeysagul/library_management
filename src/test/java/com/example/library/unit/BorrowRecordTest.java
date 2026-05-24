package com.example.library.unit;

import com.example.library.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UNIT TEST - Model Layer
 */
class BorrowRecordTest {

    private Book createSampleBook() {
        Book book = new Book("978-0-13-468599-1", "Clean Code", "Robert C. Martin", 3, Genre.TECHNOLOGY);
        book.setId(1L);
        return book;
    }

    private Member createSampleMember() {
        Member member = new Member("Alice", "alice@example.com", MembershipType.STANDARD);
        member.setId(1L);
        return member;
    }

    // =========================================================================
    // EXAMPLE: calculateFine() tests — filled in as reference
    // =========================================================================

    @Nested
    @DisplayName("calculateFine()")
    class CalculateFineTests {

        @Test
        @DisplayName("should return 0 when book is returned on time")
        void shouldReturnZeroFine_WhenReturnedOnTime() {
            BorrowRecord record = new BorrowRecord(createSampleBook(), createSampleMember());
            record.setReturnDate(record.getDueDate()); // returned exactly on due date

            assertEquals(0.0, record.calculateFine());
        }

        @Test
        @DisplayName("should return 0 when book is returned before due date")
        void shouldReturnZeroFine_WhenReturnedEarly() {
            BorrowRecord record = new BorrowRecord(createSampleBook(), createSampleMember());
            record.setReturnDate(record.getBorrowDate().plusDays(5)); // returned after 5 days

            assertEquals(0.0, record.calculateFine());
        }

        @Test
        @DisplayName("should calculate correct fine when returned 3 days late")
        void shouldCalculateCorrectFine_WhenReturnedLate() {
            BorrowRecord record = new BorrowRecord(createSampleBook(), createSampleMember());
            record.setReturnDate(record.getDueDate().plusDays(3)); // 3 days late

            double expectedFine = 3 * BorrowRecord.DAILY_FINE_RATE; // 3 * 1.50 = 4.50
            assertEquals(expectedFine, record.calculateFine());
        }

        @Test
        @DisplayName("should return 0 when book is not yet returned")
        void shouldReturnZeroFine_WhenNotYetReturned() {
            BorrowRecord record = new BorrowRecord(createSampleBook(), createSampleMember());
            // returnDate is null

            assertEquals(0.0, record.calculateFine());
        }
    }

    // =========================================================================
    // TODO: Students should write these tests - COMPLETED
    // =========================================================================

    @Nested
    @DisplayName("isOverdue()")
    class IsOverdueTests {

        @Test
        @DisplayName("should return true when checked after due date and still borrowed")
        void shouldBeOverdue_WhenPastDueDateAndStillBorrowed() {
            // Arrange
            BorrowRecord record = new BorrowRecord(createSampleBook(), createSampleMember());
            record.setStatus(BorrowStatus.BORROWED);
            
            // Set due date to 2 days ago (past date)
            record.setDueDate(LocalDate.now().minusDays(2)); 

            // Act & Assert
            // The book is late. It must be overdue.
            assertTrue(record.isOverdue());
        }

        @Test
        @DisplayName("should return false when checked before due date")
        void shouldNotBeOverdue_WhenBeforeDueDate() {
            // Arrange
            BorrowRecord record = new BorrowRecord(createSampleBook(), createSampleMember());
            record.setStatus(BorrowStatus.BORROWED);
            
            // Set due date to 5 days in the future
            record.setDueDate(LocalDate.now().plusDays(5)); 

            // Act & Assert
            // There is still time. It is not overdue.
            assertFalse(record.isOverdue());
        }

        @Test
        @DisplayName("should return false when book is already returned (even if past due)")
        void shouldNotBeOverdue_WhenAlreadyReturned() {
            // Arrange
            BorrowRecord record = new BorrowRecord(createSampleBook(), createSampleMember());
            
            // The book is late, but the status is RETURNED
            record.setDueDate(LocalDate.now().minusDays(5)); 
            record.setStatus(BorrowStatus.RETURNED); 

            // Act & Assert
            // Returned books are never overdue.
            assertFalse(record.isOverdue());
        }

        @Test
        @DisplayName("should return false on exactly the due date")
        void shouldNotBeOverdue_OnExactDueDate() {
            // Arrange
            BorrowRecord record = new BorrowRecord(createSampleBook(), createSampleMember());
            record.setStatus(BorrowStatus.BORROWED);
            
            // The check date (asOfDate) is the same as the due date
            LocalDate asOfDate = LocalDate.now();
            record.setDueDate(asOfDate); 

            // Act & Assert
            // You can return it today. It is not overdue yet.
            assertFalse(record.isOverdue());
        }
    }

    @Nested
    @DisplayName("Constructor / default values")
    class ConstructorTests {

        @Test
        @DisplayName("should set borrow date to today")
        void shouldSetBorrowDateToToday() {
            // Arrange & Act
            // Create a new record
            BorrowRecord record = new BorrowRecord(createSampleBook(), createSampleMember());

            // Assert
            // The borrow date must be today
            assertEquals(LocalDate.now(), record.getBorrowDate());
        }

        @Test
        @DisplayName("should set due date to 14 days from today")
        void shouldSetDueDateTo14DaysFromToday() {
            // Arrange & Act
            BorrowRecord record = new BorrowRecord(createSampleBook(), createSampleMember());

            // Assert
            // The due date must be 14 days after today
            LocalDate expectedDueDate = LocalDate.now().plusDays(14);
            assertEquals(expectedDueDate, record.getDueDate());
        }

        @Test
        @DisplayName("should set status to BORROWED")
        void shouldSetStatusToBorrowed() {
            // Arrange & Act
            BorrowRecord record = new BorrowRecord(createSampleBook(), createSampleMember());

            // Assert
            // The default status must be BORROWED
            assertEquals(BorrowStatus.BORROWED, record.getStatus());
        }
    }
}
