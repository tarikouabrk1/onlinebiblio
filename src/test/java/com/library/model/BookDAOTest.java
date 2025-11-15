package com.library.dao;

import com.library.model.Book;
import com.library.util.DatabaseConnection;
import org.junit.*;
import org.mockito.*;

import java.sql.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class BookDAOTest {

    @Mock
    Connection mockConnection;
    @Mock
    PreparedStatement mockPreparedStatement;
    @Mock
    Statement mockStatement;
    @Mock
    ResultSet mockResultSet;

    private BookDAO bookDAO;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Mock de la connexion
        bookDAO = new BookDAO();
        DatabaseConnection.setTestConnection(mockConnection); // à créer dans DatabaseConnection pour tests
    }

    @Test
    public void testFindById_exists() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("title")).thenReturn("Test Book");

        Book book = bookDAO.findById(1);

        assertNotNull(book);
        assertEquals(1, book.getId());
        assertEquals("Test Book", book.getTitle());
    }

    @Test
    public void testFindById_notExists() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        Book book = bookDAO.findById(99);

        assertNull(book);
    }

    @Test
    public void testCreate_success() throws Exception {
        when(mockConnection.prepareStatement(anyString(), anyInt())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        ResultSet mockKeys = mock(ResultSet.class);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockKeys);
        when(mockKeys.next()).thenReturn(true);
        when(mockKeys.getInt(1)).thenReturn(10);

        Book book = new Book();
        book.setTitle("New Book");
        boolean result = bookDAO.create(book);

        assertTrue(result);
        assertEquals(10, book.getId());
    }

    @Test
    public void testCreate_fail() throws Exception {
        when(mockConnection.prepareStatement(anyString(), anyInt())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        Book book = new Book();
        boolean result = bookDAO.create(book);

        assertFalse(result);
    }

    @Test
    public void testDelete_success() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        assertTrue(bookDAO.delete(1));
    }

    @Test
    public void testDelete_fail() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        assertFalse(bookDAO.delete(99));
    }

    @Test
    public void testIncreaseAvailableQuantity_success() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        assertTrue(bookDAO.increaseAvailableQuantity(1));
    }

    @Test
    public void testIncreaseAvailableQuantity_fail() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        assertFalse(bookDAO.increaseAvailableQuantity(99));
    }

    @Test
    public void testSQLExceptionHandling() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException());

        Book book = bookDAO.findById(1);
        assertNull(book); // doit retourner null même en cas d'exception
    }
}
