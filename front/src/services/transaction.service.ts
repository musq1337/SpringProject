import api from './api';
import type { Transaction, TransactionRequest, PageResponse, TransactionType } from '../types';

interface TransactionFilters {
  type?: TransactionType;
  categoryId?: number;
  startDate?: string;
  endDate?: string;
  minAmount?: number;
  maxAmount?: number;
  tags?: string[];
  page?: number;
  size?: number;
  sort?: string;
}

export const transactionService = {
  getAll: async (filters?: TransactionFilters): Promise<PageResponse<Transaction>> => {
    const response = await api.get<PageResponse<Transaction>>('/transactions', {
      params: filters,
    });
    return response.data;
  },

  getById: async (id: number): Promise<Transaction> => {
    const response = await api.get<Transaction>(`/transactions/${id}`);
    return response.data;
  },

  create: async (data: TransactionRequest): Promise<Transaction> => {
    const response = await api.post<Transaction>('/transactions', data);
    return response.data;
  },

  update: async (id: number, data: TransactionRequest): Promise<Transaction> => {
    const response = await api.put<Transaction>(`/transactions/${id}`, data);
    return response.data;
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/transactions/${id}`);
  },

  getBalance: async (): Promise<number> => {
    const response = await api.get<{ balance: number }>('/transactions/balance');
    return response.data.balance;
  },
};
