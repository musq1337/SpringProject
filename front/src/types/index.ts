export const Currency = {
  KZT: 'KZT',
  USD: 'USD',
  EUR: 'EUR'
} as const;

export type Currency = typeof Currency[keyof typeof Currency];

export const TransactionType = {
  INCOME: 'INCOME',
  EXPENSE: 'EXPENSE'
} as const;

export type TransactionType = typeof TransactionType[keyof typeof TransactionType];

export const BudgetPeriod = {
  MONTHLY: 'MONTHLY',
  YEARLY: 'YEARLY'
} as const;

export type BudgetPeriod = typeof BudgetPeriod[keyof typeof BudgetPeriod];

export interface User {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  defaultCurrency: Currency;
  createdAt: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  defaultCurrency: Currency;
}

export interface Category {
  id: number;
  name: string;
  type: TransactionType;
  color?: string;
  icon?: string;
  isDefault: boolean;
  userId: number;
  createdAt: string;
}

export interface CategoryRequest {
  name: string;
  type: TransactionType;
  color?: string;
  icon?: string;
}

export interface Transaction {
  id: number;
  amount: number;
  description?: string;
  date: string;
  type: TransactionType;
  categoryId: number;
  categoryName: string;
  userId: number;
  tags?: string[];
  createdAt: string;
  updatedAt: string;
}

export interface TransactionRequest {
  amount: number;
  description?: string;
  date: string;
  type: TransactionType;
  categoryId: number;
  tags?: string[];
}

export interface Budget {
  id: number;
  categoryId: number;
  categoryName: string;
  categoryColor?: string;
  limitAmount: number;
  spent: number;
  remaining: number;
  percentUsed: number;
  period: BudgetPeriod;
  startDate: string;
  endDate: string;
  daysLeft: number;
  isExceeded: boolean;
  createdAt: string;
}

export interface BudgetRequest {
  categoryId: number;
  limitAmount: number;
  period: BudgetPeriod;
  startDate: string;
}

export interface BudgetStatus {
  budget: Budget;
  spent: number;
  remaining: number;
  percentageUsed: number;
  isExceeded: boolean;
}

export interface DashboardData {
  totalIncome: number;
  totalExpense: number;
  balance: number;
  categoryExpenses: CategoryExpense[];
  recentTransactions: Transaction[];
  budgetStatuses: BudgetStatus[];
}

export interface CategoryExpense {
  categoryId: number;
  categoryName: string;
  totalAmount: number;
  color?: string;
}

export interface BalanceTrend {
  date: string;
  income: number;
  expense: number;
  balance: number;
}

export interface PageResponse<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}
