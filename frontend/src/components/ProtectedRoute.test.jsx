import React from 'react';
import { render, screen } from '@testing-library/react';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import ProtectedRoute from './ProtectedRoute';

// Mock AuthContext
const mockUseAuth = jest.fn();
jest.mock('../contexts/AuthContext', () => ({
  useAuth: () => mockUseAuth(),
}));

// Mock Navigate
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  Navigate: ({ to }) => <div data-testid="navigate" data-to={to}>Navigate to {to}</div>,
}));

describe('ProtectedRoute Component', () => {
  const TestComponent = () => <div>Protected Content</div>;

  const renderProtectedRoute = (authState) => {
    mockUseAuth.mockReturnValue(authState);
    return render(
      <MemoryRouter initialEntries={['/protected']}>
        <Routes>
          <Route path="/" element={<div>Home</div>} />
          <Route path="/protected" element={
            <ProtectedRoute>
              <TestComponent />
            </ProtectedRoute>
          } />
        </Routes>
      </MemoryRouter>
    );
  };

  test('renders loading state when loading is true', () => {
    renderProtectedRoute({ isAuthenticated: false, loading: true });

    expect(screen.getByText('Загрузка...')).toBeInTheDocument();
  });

  test('renders children when authenticated and not loading', () => {
    renderProtectedRoute({ isAuthenticated: true, loading: false });

    expect(screen.getByText('Protected Content')).toBeInTheDocument();
  });

  test('redirects to login when not authenticated and not loading', () => {
    renderProtectedRoute({ isAuthenticated: false, loading: false });

    const navigateElement = screen.getByTestId('navigate');
    expect(navigateElement).toBeInTheDocument();
    expect(navigateElement).toHaveAttribute('data-to', '/login');
  });

  test('does not render children when not authenticated', () => {
    renderProtectedRoute({ isAuthenticated: false, loading: false });

    expect(screen.queryByText('Protected Content')).not.toBeInTheDocument();
  });

  test('does not show loading when authenticated', () => {
    renderProtectedRoute({ isAuthenticated: true, loading: false });

    expect(screen.queryByText('Загрузка...')).not.toBeInTheDocument();
  });

  test('shows loading even when authenticated if loading is true', () => {
    renderProtectedRoute({ isAuthenticated: true, loading: true });

    expect(screen.getByText('Загрузка...')).toBeInTheDocument();
    expect(screen.queryByText('Protected Content')).not.toBeInTheDocument();
  });
});
