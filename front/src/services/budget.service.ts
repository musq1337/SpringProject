import api from './api';
import type { Budget, BudgetRequest, BudgetStatus } from '../types';

export const budgetService = {
  getAll: async (): Promise<Budget[]> => {
    const response = await api.get<Budget[]>('/budgets');
    return response.data;
  },

  getById: async (id: number): Promise<Budget> => {
    const response = await api.get<Budget>(`/budgets/${id}`);
    return response.data;
  },

  create: async (data: BudgetRequest): Promise<Budget> => {
    const response = await api.post<Budget>('/budgets', data);
    return response.data;
  },

  update: async (id: number, data: BudgetRequest): Promise<Budget> => {
    const response = await api.put<Budget>(`/budgets/${id}`, data);
    return response.data;
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/budgets/${id}`);
  },

  getStatus: async (id: number): Promise<BudgetStatus> => {
    const response = await api.get<BudgetStatus>(`/budgets/${id}/status`);
    return response.data;
  },

  getAllStatuses: async (): Promise<BudgetStatus[]> => {
    const response = await api.get<BudgetStatus[]>('/budgets/statuses');
    return response.data;
  },
};
