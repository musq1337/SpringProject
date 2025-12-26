import api from './api';
import type { DashboardData, CategoryExpense, BalanceTrend } from '../types';

export const analyticsService = {
  getDashboard: async (): Promise<DashboardData> => {
    const response = await api.get<DashboardData>('/analytics/dashboard');
    return response.data;
  },

  getCategoryExpenses: async (startDate?: string, endDate?: string): Promise<CategoryExpense[]> => {
    const response = await api.get<CategoryExpense[]>('/analytics/category-expenses', {
      params: { startDate, endDate },
    });
    return response.data;
  },

  getBalanceTrend: async (months: number = 6): Promise<BalanceTrend[]> => {
    const response = await api.get<BalanceTrend[]>('/analytics/balance-trend', {
      params: { months },
    });
    return response.data;
  },
};
