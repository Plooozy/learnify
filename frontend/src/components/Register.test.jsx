import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import Register from './Register';

// Mock AuthContext
const mockRegister = jest.fn();
const mockLogin = jest.fn();
jest.mock('../contexts/AuthContext', () => ({
  useAuth: () => ({
    register: mockRegister,
    login: mockLogin,
  }),
}));

// Mock useNavigate
const mockedNavigate = jest.fn();
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => mockedNavigate,
}));

describe('Register Component', () => {
  beforeEach(() => {
    mockRegister.mockClear();
    mockLogin.mockClear();
    mockedNavigate.mockClear();
  });

  const renderRegister = () => {
    return render(
      <BrowserRouter>
        <Register />
      </BrowserRouter>
    );
  };

  test('renders registration form', () => {
    renderRegister();
    expect(screen.getByText('Регистрация (React)')).toBeInTheDocument();
    expect(screen.getAllByDisplayValue('').length).toBe(2);
    expect(screen.getByText('Зарегистрироваться')).toBeInTheDocument();
  });

  test('updates form data on input change', () => {
    renderRegister();

    const inputs = screen.getAllByRole('textbox');
    const usernameInput = inputs[0];
    const passwordInput = screen.getAllByDisplayValue('')[1];

    fireEvent.change(usernameInput, { target: { value: 'testuser' } });
    fireEvent.change(passwordInput, { target: { value: 'password123' } });

    expect(usernameInput.value).toBe('testuser');
    expect(passwordInput.value).toBe('password123');
  });

  test('calls register on form submit with correct credentials', async () => {
    mockRegister.mockResolvedValue({ success: true });

    renderRegister();

    const inputs = screen.getAllByRole('textbox');
    const usernameInput = inputs[0];
    const passwordInput = screen.getAllByDisplayValue('')[1];
    const submitButton = screen.getByText('Зарегистрироваться');

    fireEvent.change(usernameInput, { target: { value: 'testuser' } });
    fireEvent.change(passwordInput, { target: { value: 'password123' } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(mockRegister).toHaveBeenCalledWith('testuser', 'password123');
    });
  });

  test('shows success message on successful registration', async () => {
    mockRegister.mockResolvedValue({ success: true });

    renderRegister();

    const inputs = screen.getAllByRole('textbox');
    const usernameInput = inputs[0];
    const passwordInput = screen.getAllByDisplayValue('')[1];
    const submitButton = screen.getByText('Зарегистрироваться');

    fireEvent.change(usernameInput, { target: { value: 'testuser' } });
    fireEvent.change(passwordInput, { target: { value: 'password123' } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText('Регистрация выполнена успешно! Выполняется вход...')).toBeInTheDocument();
    });
  });

  test('navigates to chat after successful registration and login', async () => {
    mockRegister.mockResolvedValue({ success: true });
    mockLogin.mockResolvedValue({ success: true });

    renderRegister();

    const inputs = screen.getAllByRole('textbox');
    const usernameInput = inputs[0];
    const passwordInput = screen.getAllByDisplayValue('')[1];
    const submitButton = screen.getByText('Зарегистрироваться');

    fireEvent.change(usernameInput, { target: { value: 'testuser' } });
    fireEvent.change(passwordInput, { target: { value: 'password123' } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(mockedNavigate).toHaveBeenCalledWith('/chat');
    }, { timeout: 2000 });
  });

  test('shows error message on failed registration', async () => {
    mockRegister.mockResolvedValue({ success: false, error: 'Пользователь уже существует' });

    renderRegister();

    const inputs = screen.getAllByRole('textbox');
    const usernameInput = inputs[0];
    const passwordInput = screen.getAllByDisplayValue('')[1];
    const submitButton = screen.getByText('Зарегистрироваться');

    fireEvent.change(usernameInput, { target: { value: 'existinguser' } });
    fireEvent.change(passwordInput, { target: { value: 'password123' } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText('Пользователь уже существует')).toBeInTheDocument();
    });
  });

  test('displays error message in red color', async () => {
    mockRegister.mockResolvedValue({ success: false, error: 'Ошибка регистрации' });

    renderRegister();

    const inputs = screen.getAllByRole('textbox');
    const usernameInput = inputs[0];
    const passwordInput = screen.getAllByDisplayValue('')[1];
    const submitButton = screen.getByText('Зарегистрироваться');

    fireEvent.change(usernameInput, { target: { value: 'testuser' } });
    fireEvent.change(passwordInput, { target: { value: 'password' } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      const errorMessage = screen.getByText('Ошибка регистрации');
      expect(errorMessage).toHaveStyle({ color: 'red' });
    });
  });

  test('displays success message in green color', async () => {
    mockRegister.mockResolvedValue({ success: true });

    renderRegister();

    const inputs = screen.getAllByRole('textbox');
    const usernameInput = inputs[0];
    const passwordInput = screen.getAllByDisplayValue('')[1];
    const submitButton = screen.getByText('Зарегистрироваться');

    fireEvent.change(usernameInput, { target: { value: 'testuser' } });
    fireEvent.change(passwordInput, { target: { value: 'password123' } });
    fireEvent.click(submitButton);

    await waitFor(() => {
      const successMessage = screen.getByText('Регистрация выполнена успешно! Выполняется вход...');
      expect(successMessage).toHaveStyle({ color: 'green' });
    });
  });
});
