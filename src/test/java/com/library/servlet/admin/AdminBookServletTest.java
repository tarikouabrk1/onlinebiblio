package com.library.servlet.admin;

import com.library.dao.BookDAO;
import com.library.model.Book;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

public class AdminBookServletTest {

    private AdminBookServlet servlet;
    private BookDAO bookDAO;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private RequestDispatcher dispatcher;

    @Before
    public void setUp() {
        servlet = new AdminBookServlet();
        bookDAO = mock(BookDAO.class);
        servlet.setBookDAO(bookDAO); // inject mocked DAO

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        dispatcher = mock(RequestDispatcher.class);
    }


    @Test
    public void testListBooks() throws Exception {
        Book book1 = new Book();
        book1.setTitle("Book1");
        Book book2 = new Book();
        book2.setTitle("Book2");

        List<Book> books = Arrays.asList(book1, book2);
        when(bookDAO.findAll()).thenReturn(books);
        when(request.getRequestDispatcher("/admin/books.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute("books", books);
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void testShowEditForm() throws Exception {
        Book book = new Book();
        book.setId(1);
        when(request.getParameter("action")).thenReturn("edit");
        when(request.getParameter("id")).thenReturn("1");
        when(bookDAO.findById(1)).thenReturn(book);
        when(request.getRequestDispatcher("/admin/book-form.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute("book", book);
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void testCreateBookSuccess() throws ServletException, IOException {
        when(request.getParameter("action")).thenReturn("create");
        mockBookRequestParameters();

        when(bookDAO.create(any(Book.class))).thenReturn(true);

        servlet.doPost(request, response);

        verify(response).sendRedirect("books?success=Book created successfully");
    }


    @Test
    public void testCreateBookFailure() throws ServletException, IOException {
        when(request.getParameter("action")).thenReturn("create");
        mockBookRequestParameters();

        when(bookDAO.create(any(Book.class))).thenReturn(false);

        servlet.doPost(request, response);

        verify(response).sendRedirect("books?error=Failed to create book");
    }


    @Test
    public void testUpdateBookSuccess() throws ServletException, IOException {
        when(request.getParameter("action")).thenReturn("update");
        when(request.getParameter("id")).thenReturn("1");
        mockBookRequestParameters();

        when(bookDAO.update(any(Book.class))).thenReturn(true);

        servlet.doPost(request, response);

        verify(response).sendRedirect("books?success=Book updated successfully");
    }


    @Test
    public void testDeleteBookSuccess() throws ServletException, IOException {
        when(request.getParameter("action")).thenReturn("delete");
        when(request.getParameter("id")).thenReturn("1");

        when(bookDAO.delete(1)).thenReturn(true);

        servlet.doGet(request, response);

        verify(response).sendRedirect("books?success=Book deleted successfully");
    }


    private void mockBookRequestParameters() {
        when(request.getParameter("title")).thenReturn("Title");
        when(request.getParameter("author")).thenReturn("Author");
        when(request.getParameter("isbn")).thenReturn("12345");
        when(request.getParameter("category")).thenReturn("Category");
        when(request.getParameter("description")).thenReturn("Description");
        when(request.getParameter("publisher")).thenReturn("Publisher");
        when(request.getParameter("publishedYear")).thenReturn("2020");
        when(request.getParameter("pages")).thenReturn("100");
        when(request.getParameter("language")).thenReturn("English");
        when(request.getParameter("quantity")).thenReturn("10");
        when(request.getParameter("availableQuantity")).thenReturn("10");
        when(request.getParameter("coverImage")).thenReturn("cover.jpg");
    }
}
