import React, { createContext, useContext, useState, useEffect } from 'react';
import { authService, LoginRequest, LoginResponse } from '@/services/auth.service';
import { useToast } from '@/hooks/use-toast';

interface AuthContextType {
  isAuthenticated: boolean;
  userRole: string | null;
  username: string | null;
  login: (credentials: LoginRequest) => Promise<boolean>;
  logout: () => void;
  loading: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);
  const [userRole, setUserRole] = useState<string | null>(null);
  const [username, setUsername] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const { toast } = useToast();

  // Check for existing session on mount
  useEffect(() => {
    const storedRole = localStorage.getItem('userRole');
    const storedUsername = localStorage.getItem('username');
    if (storedRole && storedUsername) {
      setIsAuthenticated(true);
      setUserRole(storedRole);
      setUsername(storedUsername);
    }
    setLoading(false);
  }, []);

  const login = async (credentials: LoginRequest): Promise<boolean> => {
    try {
      const response: LoginResponse = await authService.login(credentials);
      
      if (response.success) {
        setIsAuthenticated(true);
        setUserRole(response.role);
        setUsername(credentials.username);
        
        // Store in localStorage for persistence
        localStorage.setItem('userRole', response.role);
        localStorage.setItem('username', credentials.username);
        
        toast({
          title: 'Success',
          description: response.message || 'Logged in successfully',
        });
        
        return true;
      } else {
        toast({
          title: 'Error',
          description: 'Login failed',
          variant: 'destructive',
        });
        return false;
      }
    } catch (error: any) {
      console.error("Login Error:", error); // Helpful for debugging

      // --- FIX STARTS HERE ---
      // The backend returns an object { error, message }, so we need to grab .message
      // We also handle cases where it might be a string or undefined
      const responseData = error.response?.data;
      
      let errorMessage = 'Invalid username or password';
      
      if (typeof responseData === 'string') {
        errorMessage = responseData;
      } else if (responseData && typeof responseData === 'object' && responseData.message) {
        errorMessage = responseData.message;
      }

      toast({
        title: 'Error',
        description: errorMessage, // This is now guaranteed to be a string
        variant: 'destructive',
      });
      return false;
    }
  };

  const logout = () => {
    authService.logout();
    setIsAuthenticated(false);
    setUserRole(null);
    setUsername(null);
    toast({
      title: 'Logged out',
      description: 'You have been logged out successfully',
    });
  };

  return (
    <AuthContext.Provider
      value={{
        isAuthenticated,
        userRole,
        username,
        login,
        logout,
        loading,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

