import api from './api';
import type { Category, CategoryRequest, TransactionType } from '../types';

export const categoryService = {
  getAll: async (type?: TransactionType): Promise<Category[]> => {
    const params = type ? { type } : {};
    const response = await api.get<Category[]>('/categories', { params });
    return response.data;
  },

  getById: async (id: number): Promise<Category> => {
    const response = await api.get<Category>(`/categories/${id}`);
    return response.data;
  },

  create: async (data: CategoryRequest): Promise<Category> => {
    const response = await api.post<Category>('/categories', data);
    return response.data;
  },

  update: async (id: number, data: CategoryRequest): Promise<Category> => {
    const response = await api.put<Category>(`/categories/${id}`, data);
    return response.data;
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/categories/${id}`);
  },
};
