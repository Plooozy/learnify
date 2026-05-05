import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import Chat from './Chat';
import * as apiModule from '../services/api';

// Mock the api module
jest.mock('../services/api');

// Mock the useAuth hook
const mockLogout = jest.fn();
jest.mock('../contexts/AuthContext', () => ({
  useAuth: () => ({
    user: { username: 'testuser' },
    logout: mockLogout,
  }),
  AuthProvider: ({ children }) => children,
}));

describe('Chat Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  const renderChat = () => {
    return render(
      <BrowserRouter>
        <Chat />
      </BrowserRouter>
    );
  };

  test('renders chat component with initial elements', () => {
    renderChat();

    expect(screen.getByText(/Чат с AI/i)).toBeInTheDocument();
    expect(screen.getByPlaceholderText(/Введите сообщение/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /Отправить/i })).toBeInTheDocument();
  });

  test('displays user information', () => {
    renderChat();

    expect(screen.getByText(/Пользователь: testuser/i)).toBeInTheDocument();
  });

  test('displays logout button', () => {
    renderChat();

    expect(screen.getByRole('button', { name: /Выйти/i })).toBeInTheDocument();
  });

  test('displays initial message when no messages', () => {
    renderChat();

    expect(screen.getByText(/Начните диалог/i)).toBeInTheDocument();
  });

  test('input field is initially empty', () => {
    renderChat();

    const input = screen.getByPlaceholderText(/Введите сообщение/i);
    expect(input).toHaveValue('');
  });

  test('send button is disabled when input is empty', () => {
    renderChat();

    const sendButton = screen.getByRole('button', { name: /Отправить/i });
    expect(sendButton).toBeDisabled();
  });

  test('send button is enabled when input has text', () => {
    renderChat();

    const input = screen.getByPlaceholderText(/Введите сообщение/i);
    const sendButton = screen.getByRole('button', { name: /Отправить/i });

    fireEvent.change(input, { target: { value: 'Hello' } });

    expect(sendButton).not.toBeDisabled();
  });

  test('sends message when form is submitted', async () => {
    const mockPost = jest.fn().mockResolvedValue({ data: 'AI response' });
    apiModule.default.post = mockPost;

    renderChat();

    const input = screen.getByPlaceholderText(/Введите сообщение/i);
    const sendButton = screen.getByRole('button', { name: /Отправить/i });

    fireEvent.change(input, { target: { value: 'Hello' } });
    fireEvent.click(sendButton);

    await waitFor(() => {
      expect(mockPost).toHaveBeenCalledWith('/api/chat', { message: 'Hello' });
    });
  });

  test('displays user message after sending', async () => {
    const mockPost = jest.fn().mockResolvedValue({ data: 'AI response' });
    apiModule.default.post = mockPost;

    renderChat();

    const input = screen.getByPlaceholderText(/Введите сообщение/i);
    const sendButton = screen.getByRole('button', { name: /Отправить/i });

    fireEvent.change(input, { target: { value: 'Hello' } });
    fireEvent.click(sendButton);

    await waitFor(() => {
      expect(screen.getByText(/Hello/i)).toBeInTheDocument();
    });
  });

  test('displays AI response after receiving', async () => {
    const mockPost = jest.fn().mockResolvedValue({ data: 'AI response' });
    apiModule.default.post = mockPost;

    renderChat();

    const input = screen.getByPlaceholderText(/Введите сообщение/i);
    const sendButton = screen.getByRole('button', { name: /Отправить/i });

    fireEvent.change(input, { target: { value: 'Hello' } });
    fireEvent.click(sendButton);

    await waitFor(() => {
      expect(screen.getByText(/AI response/i)).toBeInTheDocument();
    });
  });

  test('clears input after sending message', async () => {
    const mockPost = jest.fn().mockResolvedValue({ data: 'AI response' });
    apiModule.default.post = mockPost;

    renderChat();

    const input = screen.getByPlaceholderText(/Введите сообщение/i);
    const sendButton = screen.getByRole('button', { name: /Отправить/i });

    fireEvent.change(input, { target: { value: 'Hello' } });
    fireEvent.click(sendButton);

    await waitFor(() => {
      expect(input).toHaveValue('');
    });
  });

  test('displays loading indicator while sending', async () => {
    let resolvePromise;
    const mockPost = jest.fn(() => new Promise(resolve => {
      resolvePromise = resolve;
    }));
    apiModule.default.post = mockPost;

    renderChat();

    const input = screen.getByPlaceholderText(/Введите сообщение/i);
    const sendButton = screen.getByRole('button', { name: /Отправить/i });

    fireEvent.change(input, { target: { value: 'Hello' } });
    fireEvent.click(sendButton);

    expect(screen.getByText(/AI печатает/i)).toBeInTheDocument();

    resolvePromise({ data: 'AI response' });
  });

  test('displays error message when API fails', async () => {
    const mockPost = jest.fn().mockRejectedValue(new Error('API Error'));
    apiModule.default.post = mockPost;

    renderChat();

    const input = screen.getByPlaceholderText(/Введите сообщение/i);
    const sendButton = screen.getByRole('button', { name: /Отправить/i });

    fireEvent.change(input, { target: { value: 'Hello' } });
    fireEvent.click(sendButton);

    await waitFor(() => {
      expect(screen.getByText(/произошла ошибка/i)).toBeInTheDocument();
    });
  });

  test('prevents sending empty messages', async () => {
    const mockPost = jest.fn().mockResolvedValue({ data: 'AI response' });
    apiModule.default.post = mockPost;

    renderChat();

    const sendButton = screen.getByRole('button', { name: /Отправить/i });

    fireEvent.click(sendButton);

    await waitFor(() => {
      expect(mockPost).not.toHaveBeenCalled();
    });
  });

  test('prevents sending while already sending', async () => {
    let resolvePromise;
    const mockPost = jest.fn(() => new Promise(resolve => {
      resolvePromise = resolve;
    }));
    apiModule.default.post = mockPost;

    renderChat();

    const input = screen.getByPlaceholderText(/Введите сообщение/i);
    const sendButton = screen.getByRole('button', { name: /Отправить/i });

    fireEvent.change(input, { target: { value: 'Hello' } });
    fireEvent.click(sendButton);

    // Try to send another message while first is loading
    fireEvent.change(input, { target: { value: 'Second message' } });
    fireEvent.click(sendButton);

    await waitFor(() => {
      expect(mockPost).toHaveBeenCalledTimes(1);
    });

    resolvePromise({ data: 'AI response' });
  });

  test('handles special characters in messages', async () => {
    const mockPost = jest.fn().mockResolvedValue({ data: 'Response' });
    apiModule.default.post = mockPost;

    renderChat();

    const input = screen.getByPlaceholderText(/Введите сообщение/i);
    const sendButton = screen.getByRole('button', { name: /Отправить/i });

    fireEvent.change(input, { target: { value: 'Hello! @#$%^&*()' } });
    fireEvent.click(sendButton);

    await waitFor(() => {
      expect(mockPost).toHaveBeenCalledWith('/api/chat', { message: 'Hello! @#$%^&*()' });
    });
  });

  test('handles unicode characters in messages', async () => {
    const mockPost = jest.fn().mockResolvedValue({ data: 'Response' });
    apiModule.default.post = mockPost;

    renderChat();

    const input = screen.getByPlaceholderText(/Введите сообщение/i);
    const sendButton = screen.getByRole('button', { name: /Отправить/i });

    fireEvent.change(input, { target: { value: 'Привет! 你好! こんにちは!' } });
    fireEvent.click(sendButton);

    await waitFor(() => {
      expect(mockPost).toHaveBeenCalledWith('/api/chat', { message: 'Привет! 你好! こんにちは!' });
    });
  });

  test('displays message timestamps', async () => {
    const mockPost = jest.fn().mockResolvedValue({ data: 'AI response' });
    apiModule.default.post = mockPost;

    renderChat();

    const input = screen.getByPlaceholderText(/Введите сообщение/i);
    const sendButton = screen.getByRole('button', { name: /Отправить/i });

    fireEvent.change(input, { target: { value: 'Hello' } });
    fireEvent.click(sendButton);

    await waitFor(() => {
      const timestamps = screen.getAllByText(/\d{2}:\d{2}/);
      expect(timestamps.length).toBeGreaterThan(0);
    });
  });

  test('displays multiple messages in conversation', async () => {
    const mockPost = jest.fn()
      .mockResolvedValueOnce({ data: 'First response' })
      .mockResolvedValueOnce({ data: 'Second response' });
    apiModule.default.post = mockPost;

    renderChat();

    const input = screen.getByPlaceholderText(/Введите сообщение/i);
    const sendButton = screen.getByRole('button', { name: /Отправить/i });

    // Send first message
    fireEvent.change(input, { target: { value: 'First message' } });
    fireEvent.click(sendButton);

    await waitFor(() => {
      expect(screen.getByText(/First message/i)).toBeInTheDocument();
      expect(screen.getByText(/First response/i)).toBeInTheDocument();
    });

    // Send second message
    fireEvent.change(input, { target: { value: 'Second message' } });
    fireEvent.click(sendButton);

    await waitFor(() => {
      expect(screen.getByText(/Second message/i)).toBeInTheDocument();
      expect(screen.getByText(/Second response/i)).toBeInTheDocument();
    });
  });

  test('logout button calls logout function', () => {
    renderChat();

    const logoutButton = screen.getByRole('button', { name: /Выйти/i });
    fireEvent.click(logoutButton);

    expect(mockLogout).toHaveBeenCalled();
  });
});