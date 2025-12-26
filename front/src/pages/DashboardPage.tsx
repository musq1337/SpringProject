import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { analyticsService } from '../services/analytics.service';
import { transactionService } from '../services/transaction.service';
import { categoryService } from '../services/category.service';
import { formatCurrency, formatDate } from '../utils/format';
import { PieChart, Pie, Cell, ResponsiveContainer, Tooltip } from 'recharts';
import { Wallet, ArrowUpRight, ArrowDownRight, Plus, X } from 'lucide-react';
import type { TransactionRequest } from '../types';

export default function DashboardPage() {
  const [showAddModal, setShowAddModal] = useState(false);
  const queryClient = useQueryClient();

  // ALL hooks must be declared BEFORE any conditional returns
  const { register, handleSubmit, reset, watch, formState: { errors } } = useForm<TransactionRequest>({
    defaultValues: {
      date: new Date().toISOString().split('T')[0],
      type: 'EXPENSE',
    }
  });

  const transactionType = watch('type');

  const { data: dashboard, isLoading } = useQuery({
    queryKey: ['dashboard'],
    queryFn: () => analyticsService.getDashboard(),
  });

  const { data: balance } = useQuery({
    queryKey: ['balance'],
    queryFn: () => transactionService.getBalance(),
  });

  const { data: transactions } = useQuery({
    queryKey: ['transactions'],
    queryFn: () => transactionService.getAll({ page: 0, size: 20 }),
  });

  const { data: categories = [] } = useQuery({
    queryKey: ['categories'],
    queryFn: () => categoryService.getAll(),
  });

  const createMutation = useMutation({
    mutationFn: transactionService.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['transactions'] });
      queryClient.invalidateQueries({ queryKey: ['dashboard'] });
      queryClient.invalidateQueries({ queryKey: ['balance'] });
      setShowAddModal(false);
      reset();
    },
  });

  const deleteMutation = useMutation({
    mutationFn: transactionService.delete,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['transactions'] });
      queryClient.invalidateQueries({ queryKey: ['dashboard'] });
      queryClient.invalidateQueries({ queryKey: ['balance'] });
    },
  });

  // NOW it's safe to do conditional returns
  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-lg text-gray-600">Загрузка...</div>
      </div>
    );
  }

  console.log('Dashboard data:', { dashboard, balance, transactions, categories });

  const chartData = dashboard?.categoryExpenses?.map((item) => ({
    name: item.categoryName,
    value: item.amount,
  })) || [];

  const COLORS = ['#3b82f6', '#8b5cf6', '#ec4899', '#f43f5e', '#f97316', '#10b981'];

  const filteredCategories = categories.filter(cat => cat.type === transactionType);

  const onSubmit = (data: TransactionRequest) => {
    createMutation.mutate({
      ...data,
      categoryId: Number(data.categoryId),
      amount: Number(data.amount),
    });
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold bg-gradient-to-r from-gray-900 to-gray-600 bg-clip-text text-transparent">
            Панель управления
          </h1>
          <p className="text-gray-600 mt-1">Обзор ваших финансов</p>
        </div>
        <button
          onClick={() => setShowAddModal(true)}
          className="flex items-center gap-2 bg-gradient-to-r from-blue-600 to-indigo-600 text-white px-6 py-3 rounded-xl font-semibold shadow-lg hover:shadow-xl transform hover:-translate-y-0.5 transition-all duration-200"
        >
          <Plus className="w-5 h-5" />
          Добавить транзакцию
        </button>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="bg-gradient-to-br from-green-50 to-emerald-50 rounded-2xl shadow-lg p-6 border border-green-100 hover:shadow-xl transition-all duration-300 transform hover:-translate-y-1">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-semibold text-green-700">Доходы</p>
              <p className="text-3xl font-bold text-green-600 mt-2">
                {formatCurrency(dashboard?.totalIncome || 0)}
              </p>
            </div>
            <div className="bg-green-500 rounded-2xl p-4 shadow-lg">
              <ArrowUpRight className="w-8 h-8 text-white" />
            </div>
          </div>
        </div>

        <div className="bg-gradient-to-br from-red-50 to-pink-50 rounded-2xl shadow-lg p-6 border border-red-100 hover:shadow-xl transition-all duration-300 transform hover:-translate-y-1">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-semibold text-red-700">Расходы</p>
              <p className="text-3xl font-bold text-red-600 mt-2">
                {formatCurrency(dashboard?.totalExpense || 0)}
              </p>
            </div>
            <div className="bg-red-500 rounded-2xl p-4 shadow-lg">
              <ArrowDownRight className="w-8 h-8 text-white" />
            </div>
          </div>
        </div>

        <div className="bg-gradient-to-br from-blue-50 to-indigo-50 rounded-2xl shadow-lg p-6 border border-blue-100 hover:shadow-xl transition-all duration-300 transform hover:-translate-y-1">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-semibold text-blue-700">Баланс</p>
              <p className="text-3xl font-bold text-blue-600 mt-2">
                {formatCurrency(balance || 0)}
              </p>
            </div>
            <div className="bg-blue-500 rounded-2xl p-4 shadow-lg">
              <Wallet className="w-8 h-8 text-white" />
            </div>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Category Expenses Chart */}
        <div className="bg-white rounded-2xl shadow-lg p-6 border border-gray-100">
          <h2 className="text-xl font-bold text-gray-900 mb-6">Расходы по категориям</h2>
          {chartData.length > 0 ? (
            <ResponsiveContainer width="100%" height={300}>
              <PieChart>
                <Pie
                  data={chartData}
                  cx="50%"
                  cy="50%"
                  labelLine={false}
                  label={({ name, percent }) => percent ? `${name}: ${(percent * 100).toFixed(0)}%` : name}
                  outerRadius={80}
                  fill="#8884d8"
                  dataKey="value"
                >
                  {chartData.map((_entry, index) => (
                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip formatter={(value) => formatCurrency(value as number)} />
              </PieChart>
            </ResponsiveContainer>
          ) : (
            <div className="flex flex-col items-center justify-center py-12">
              <div className="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mb-3">
                <svg className="w-8 h-8 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
                </svg>
              </div>
              <p className="text-gray-500">Нет данных для отображения</p>
            </div>
          )}
        </div>

        {/* All Transactions */}
        <div className="bg-white rounded-2xl shadow-lg p-6 border border-gray-100">
          <h2 className="text-xl font-bold text-gray-900 mb-6">Все транзакции</h2>
          <div className="space-y-3">
            {transactions?.content?.map((transaction) => (
              <div
                key={transaction.id}
                className="flex items-center justify-between p-4 bg-gradient-to-r from-gray-50 to-gray-100 rounded-xl hover:shadow-md transition-shadow duration-200 group"
              >
                <div className="flex-1">
                  <p className="font-semibold text-gray-900">{transaction.categoryName}</p>
                  <p className="text-sm text-gray-600 mt-1">
                    {transaction.description || 'Без описания'}
                  </p>
                  <p className="text-xs text-gray-400 mt-1">{formatDate(transaction.date)}</p>
                </div>
                <div className="flex items-center gap-3">
                  <div className={`text-lg font-bold px-4 py-2 rounded-xl ${
                    transaction.type === 'INCOME' 
                      ? 'bg-green-100 text-green-700' 
                      : 'bg-red-100 text-red-700'
                  }`}>
                    {transaction.type === 'INCOME' ? '+' : '-'}
                    {formatCurrency(transaction.amount)}
                  </div>
                  <button
                    onClick={() => {
                      if (confirm('Удалить эту транзакцию?')) {
                        deleteMutation.mutate(transaction.id);
                      }
                    }}
                    className="opacity-0 group-hover:opacity-100 text-red-500 hover:text-red-700 transition-opacity"
                  >
                    <X className="w-5 h-5" />
                  </button>
                </div>
              </div>
            ))}
            {(!transactions?.content || transactions.content.length === 0) && (
              <div className="flex flex-col items-center justify-center py-12">
                <div className="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mb-3">
                  <svg className="w-8 h-8 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
                  </svg>
                </div>
                <p className="text-gray-500">Нет транзакций</p>
                <p className="text-sm text-gray-400 mt-1">Нажмите "Добавить транзакцию" для создания</p>
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Budget Status */}
      {dashboard?.budgetStatuses && dashboard.budgetStatuses.length > 0 && (
        <div className="bg-white rounded-2xl shadow-lg p-6 border border-gray-100">
          <h2 className="text-xl font-bold text-gray-900 mb-6">Статус бюджетов</h2>
          <div className="space-y-4">
            {dashboard.budgetStatuses.map((status) => (
              <div key={status.budget.id} className="space-y-2 p-4 bg-gradient-to-r from-gray-50 to-gray-100 rounded-xl">
                <div className="flex items-center justify-between">
                  <span className="font-semibold text-gray-900">
                    {status.budget.categoryName}
                  </span>
                  <span className="text-sm font-medium text-gray-600">
                    {formatCurrency(status.spent)} / {formatCurrency(status.budget.limitAmount)}
                  </span>
                </div>
                <div className="relative w-full bg-gray-200 rounded-full h-3 overflow-hidden">
                  <div
                    className={`h-3 rounded-full transition-all duration-500 ${
                      status.isExceeded
                        ? 'bg-gradient-to-r from-red-500 to-red-600'
                        : status.percentageUsed > 80
                        ? 'bg-gradient-to-r from-yellow-400 to-orange-500'
                        : 'bg-gradient-to-r from-green-400 to-emerald-500'
                    }`}
                    style={{ width: `${Math.min(status.percentageUsed, 100)}%` }}
                  />
                </div>
                <div className="flex justify-between items-center">
                  <span className={`text-xs font-semibold ${
                    status.isExceeded
                      ? 'text-red-600'
                      : status.percentageUsed > 80
                      ? 'text-yellow-600'
                      : 'text-green-600'
                  }`}>
                    {status.percentageUsed.toFixed(1)}% использовано
                  </span>
                  {status.isExceeded && (
                    <span className="px-3 py-1 bg-red-100 text-red-700 text-xs font-semibold rounded-full">
                      Превышен!
                    </span>
                  )}
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Add Transaction Modal */}
      {showAddModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl shadow-2xl max-w-md w-full max-h-[90vh] overflow-y-auto">
            <div className="sticky top-0 bg-white border-b border-gray-200 px-6 py-4 flex items-center justify-between rounded-t-2xl">
              <h2 className="text-xl font-bold text-gray-900">Новая транзакция</h2>
              <button
                onClick={() => setShowAddModal(false)}
                className="text-gray-400 hover:text-gray-600"
              >
                <X className="w-6 h-6" />
              </button>
            </div>

            <form onSubmit={handleSubmit(onSubmit)} className="p-6 space-y-4">
              {/* Type */}
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">
                  Тип
                </label>
                <div className="grid grid-cols-2 gap-3">
                  <label className={`flex items-center justify-center gap-2 p-4 rounded-xl border-2 cursor-pointer transition-all ${
                    transactionType === 'INCOME' 
                      ? 'border-green-500 bg-green-50' 
                      : 'border-gray-200 hover:border-gray-300'
                  }`}>
                    <input
                      type="radio"
                      value="INCOME"
                      {...register('type', { required: true })}
                      className="hidden"
                    />
                    <ArrowUpRight className="w-5 h-5 text-green-600" />
                    <span className="font-semibold">Доход</span>
                  </label>
                  <label className={`flex items-center justify-center gap-2 p-4 rounded-xl border-2 cursor-pointer transition-all ${
                    transactionType === 'EXPENSE' 
                      ? 'border-red-500 bg-red-50' 
                      : 'border-gray-200 hover:border-gray-300'
                  }`}>
                    <input
                      type="radio"
                      value="EXPENSE"
                      {...register('type', { required: true })}
                      className="hidden"
                    />
                    <ArrowDownRight className="w-5 h-5 text-red-600" />
                    <span className="font-semibold">Расход</span>
                  </label>
                </div>
              </div>

              {/* Amount */}
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">
                  Сумма
                </label>
                <input
                  type="number"
                  step="0.01"
                  {...register('amount', { required: 'Введите сумму', min: 0.01 })}
                  className="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  placeholder="0.00"
                />
                {errors.amount && (
                  <p className="text-red-500 text-sm mt-1">{errors.amount.message}</p>
                )}
              </div>

              {/* Category */}
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">
                  Категория
                </label>
                <select
                  {...register('categoryId', { required: 'Выберите категорию' })}
                  className="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                >
                  <option value="">Выберите категорию</option>
                  {filteredCategories.map(cat => (
                    <option key={cat.id} value={cat.id}>
                      {cat.icon} {cat.name}
                    </option>
                  ))}
                </select>
                {errors.categoryId && (
                  <p className="text-red-500 text-sm mt-1">{errors.categoryId.message}</p>
                )}
                {filteredCategories.length === 0 && (
                  <p className="text-yellow-600 text-sm mt-1">
                    Нет категорий для типа "{transactionType === 'INCOME' ? 'Доход' : 'Расход'}"
                  </p>
                )}
              </div>

              {/* Date */}
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">
                  Дата
                </label>
                <input
                  type="date"
                  {...register('date', { required: 'Выберите дату' })}
                  className="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                />
                {errors.date && (
                  <p className="text-red-500 text-sm mt-1">{errors.date.message}</p>
                )}
              </div>

              {/* Description */}
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">
                  Описание (необязательно)
                </label>
                <textarea
                  {...register('description')}
                  rows={3}
                  className="w-full px-4 py-3 border border-gray-300 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none"
                  placeholder="Добавьте описание..."
                />
              </div>

              {/* Submit */}
              <div className="flex gap-3 pt-4">
                <button
                  type="button"
                  onClick={() => setShowAddModal(false)}
                  className="flex-1 px-4 py-3 border border-gray-300 text-gray-700 rounded-xl font-semibold hover:bg-gray-50 transition-colors"
                >
                  Отмена
                </button>
                <button
                  type="submit"
                  disabled={createMutation.isPending || filteredCategories.length === 0}
                  className="flex-1 px-4 py-3 bg-gradient-to-r from-blue-600 to-indigo-600 text-white rounded-xl font-semibold hover:shadow-lg transition-all disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {createMutation.isPending ? 'Создание...' : 'Создать'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
