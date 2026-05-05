import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import Login from './Login';

// Mock AuthContext
const mockLogin = jest.fn();
jest.mock('../contexts/AuthContext', () => ({
  useAuth: () => ({
    login: mockLogin,
  }),
}));

// Mock useNavigate
const mockedNavigate = jest.fn();
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => mockedNavigate,
}));

describe('Login Component', () => {
  beforeEach(() => {
    mockLogin.mockClear();
    mockedNavigate.mockClear();
  });

  const renderLogin = () => {
    return render(
      <BrowserRouter>
        <Login />
      </BrowserRouter>
    );
  };

  test('renders login form', () => {
    renderLogin();
    expect(screen.getByText('Вход')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('Имя')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('Пароль')).toBeInTheDocument();
    expect(screen.getByText('Войти')).toBeInTheDocument();
  });

  test('updates form data on input change', () => {
    renderLogin();

    const usernameInput = screen.getByPlaceholderText('Имя');
    const passwordInput = screen.getByPlaceholderText('Пароль');

    fireEvent.change(usernameInput, { target: { value: 'testuser' } });
    fireEvent.change(passwordInput, { target: { value: 'password123' } });

    expect(usernameInput.value).toBe('testuser');
    expect(passwordInput.value).toBe('password123');
  });

  test('calls login on form submit with correct credentials', async () => {
    mockLogin.mockResolvedValue({ success: true });

    renderLogin();

    const usernameInput = screen.getByPlaceholderText('Имя');
    const passwordInput = screen.getByPlaceholderText('Пароль');
    const submitButton = screen.getByText('Войти');

    fireEvent.change(usernameInput, { target: { value: 'testuser' } });
    fireEvent.change(passwordInput, { target: { value: 'password123' } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(mockLogin).toHaveBeenCalledWith('testuser', 'password123');
    });
  });

  test('shows success message on successful login', async () => {
    mockLogin.mockResolvedValue({ success: true });

    renderLogin();

    const usernameInput = screen.getByPlaceholderText('Имя');
    const passwordInput = screen.getByPlaceholderText('Пароль');
    const submitButton = screen.getByText('Войти');

    fireEvent.change(usernameInput, { target: { value: 'testuser' } });
    fireEvent.change(passwordInput, { target: { value: 'password123' } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText('Вход выполнен успешно!')).toBeInTheDocument();
    });
  });

  test('navigates to chat after successful login', async () => {
    mockLogin.mockResolvedValue({ success: true });

    renderLogin();

    const usernameInput = screen.getByPlaceholderText('Имя');
    const passwordInput = screen.getByPlaceholderText('Пароль');
    const submitButton = screen.getByText('Войти');

    fireEvent.change(usernameInput, { target: { value: 'testuser' } });
    fireEvent.change(passwordInput, { target: { value: 'password123' } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(mockedNavigate).toHaveBeenCalledWith('/chat');
    }, { timeout: 2000 });
  });

  test('shows error message on failed login', async () => {
    mockLogin.mockResolvedValue({ success: false, error: 'Неверные учетные данные' });

    renderLogin();

    const usernameInput = screen.getByPlaceholderText('Имя');
    const passwordInput = screen.getByPlaceholderText('Пароль');
    const submitButton = screen.getByText('Войти');

    fireEvent.change(usernameInput, { target: { value: 'testuser' } });
    fireEvent.change(passwordInput, { target: { value: 'wrongpassword' } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText('Неверные учетные данные')).toBeInTheDocument();
    });
  });

  test('displays error message in red color', async () => {
    mockLogin.mockResolvedValue({ success: false, error: 'Ошибка входа' });

    renderLogin();

    const usernameInput = screen.getByPlaceholderText('Имя');
    const passwordInput = screen.getByPlaceholderText('Пароль');
    const submitButton = screen.getByText('Войти');

    fireEvent.change(usernameInput, { target: { value: 'testuser' } });
    fireEvent.change(passwordInput, { target: { value: 'password' } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      const errorMessage = screen.getByText('Ошибка входа');
      expect(errorMessage).toHaveStyle({ color: 'red' });
    });
  });

  test('displays success message in green color', async () => {
    mockLogin.mockResolvedValue({ success: true });

    renderLogin();

    const usernameInput = screen.getByPlaceholderText('Имя');
    const passwordInput = screen.getByPlaceholderText('Пароль');
    const submitButton = screen.getByText('Войти');

    fireEvent.change(usernameInput, { target: { value: 'testuser' } });
    fireEvent.change(passwordInput, { target: { value: 'password123' } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      const successMessage = screen.getByText('Вход выполнен успешно!');
      expect(successMessage).toHaveStyle({ color: 'green' });
    });
  });
});
