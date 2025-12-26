import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Plus, Edit2, Trash2, X, TrendingUp, Calendar, AlertTriangle } from 'lucide-react';
import { budgetService } from '../services/budget.service';
import { categoryService } from '../services/category.service';
import type { Budget, BudgetRequest, BudgetPeriod, Category } from '../types';
import { formatCurrency } from '../utils/format';
import { getCategoryColor } from '../utils/colors';

export default function BudgetsPage() {
  const queryClient = useQueryClient();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingBudget, setEditingBudget] = useState<Budget | null>(null);

  const { data: budgets = [], isLoading } = useQuery({
    queryKey: ['budgets'],
    queryFn: budgetService.getAll,
  });

  const { data: categories = [] } = useQuery({
    queryKey: ['categories'],
    queryFn: () => categoryService.getAll(),
  });

  const deleteMutation = useMutation({
    mutationFn: budgetService.delete,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['budgets'] });
    },
  });

  const handleEdit = (budget: Budget) => {
    setEditingBudget(budget);
    setIsModalOpen(true);
  };

  const handleDelete = (id: number) => {
    if (window.confirm('Вы уверены, что хотите удалить этот бюджет?')) {
      deleteMutation.mutate(id);
    }
  };

  const handleAddNew = () => {
    setEditingBudget(null);
    setIsModalOpen(true);
  };

  const getProgressPercentage = (budget: Budget): number => {
    return Math.min((budget.spent / budget.limitAmount) * 100, 100);
  };

  const getProgressColor = (percentage: number): string => {
    if (percentage >= 100) return 'bg-red-600';
    if (percentage >= 80) return 'bg-yellow-500';
    return 'bg-green-600';
  };

  const getBudgetStatus = (budget: Budget): 'ok' | 'warning' | 'exceeded' => {
    if (budget.isExceeded) return 'exceeded';
    if (budget.percentUsed >= 80) return 'warning';
    return 'ok';
  };

  const getPeriodLabel = (period: BudgetPeriod): string => {
    switch (period) {
      case 'MONTHLY':
        return 'Месяц';
      case 'YEARLY':
        return 'Год';
      default:
        return period;
    }
  };

  return (
    <div className="p-6 max-w-7xl mx-auto">
      {/* Header */}
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Бюджеты</h1>
          <p className="text-gray-600 mt-1">Установите лимиты расходов по категориям</p>
        </div>
        <button
          onClick={handleAddNew}
          className="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition"
        >
          <Plus className="w-5 h-5" />
          Добавить бюджет
        </button>
      </div>

      {isLoading ? (
        <div className="text-center py-12">
          <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
          <p className="text-gray-500 mt-2">Загрузка...</p>
        </div>
      ) : budgets.length === 0 ? (
        <div className="text-center py-12 bg-white rounded-lg border border-gray-200">
          <TrendingUp className="w-16 h-16 text-gray-400 mx-auto mb-4" />
          <p className="text-gray-500 text-lg mb-2">Нет бюджетов</p>
          <p className="text-gray-400 text-sm mb-4">
            Создайте бюджет, чтобы контролировать свои расходы
          </p>
          <button
            onClick={handleAddNew}
            className="inline-flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition"
          >
            <Plus className="w-5 h-5" />
            Создать первый бюджет
          </button>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {budgets.map((budget) => {
            const status = getBudgetStatus(budget);
            const percentage = getProgressPercentage(budget);
            const categoryColor = budget.categoryColor || getCategoryColor(budget.categoryName);

            return (
              <div
                key={budget.id}
                className="bg-white rounded-lg shadow-sm border border-gray-200 p-6 hover:shadow-md transition"
              >
                {/* Header */}
                <div className="flex items-start justify-between mb-4">
                  <div className="flex items-center gap-3">
                    <div
                      className="w-10 h-10 rounded-lg flex items-center justify-center text-lg"
                      style={{ backgroundColor: `${categoryColor}20` }}
                    >
                      📊
                    </div>
                    <div>
                      <h3 className="font-semibold text-gray-900">{budget.categoryName}</h3>
                      <div className="flex items-center gap-1 text-xs text-gray-500">
                        <Calendar className="w-3 h-3" />
                        {getPeriodLabel(budget.period)}
                      </div>
                    </div>
                  </div>
                  <div className="flex gap-1">
                    <button
                      onClick={() => handleEdit(budget)}
                      className="p-1.5 text-blue-600 hover:bg-blue-50 rounded transition"
                      title="Редактировать"
                    >
                      <Edit2 className="w-4 h-4" />
                    </button>
                    <button
                      onClick={() => handleDelete(budget.id)}
                      className="p-1.5 text-red-600 hover:bg-red-50 rounded transition"
                      title="Удалить"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </div>
                </div>

                {/* Progress Bar */}
                <div className="mb-4">
                  <div className="flex justify-between text-sm mb-2">
                    <span className="text-gray-600">Израсходовано</span>
                    <span
                      className={`font-semibold ${
                        status === 'exceeded'
                          ? 'text-red-600'
                          : status === 'warning'
                          ? 'text-yellow-600'
                          : 'text-green-600'
                      }`}
                    >
                      {percentage.toFixed(0)}%
                    </span>
                  </div>
                  <div className="w-full bg-gray-200 rounded-full h-3 overflow-hidden">
                    <div
                      className={`h-full transition-all duration-300 ${getProgressColor(
                        percentage
                      )}`}
                      style={{ width: `${percentage}%` }}
                    />
                  </div>
                </div>

                {/* Amount Info */}
                <div className="flex justify-between items-end mb-4">
                  <div>
                    <p className="text-xs text-gray-500">Потрачено</p>
                    <p className="text-lg font-bold text-gray-900">
                      {formatCurrency(budget.spent, 'KZT')}
                    </p>
                  </div>
                  <div className="text-right">
                    <p className="text-xs text-gray-500">Лимит</p>
                    <p className="text-lg font-semibold text-gray-700">
                      {formatCurrency(budget.limitAmount, 'KZT')}
                    </p>
                  </div>
                </div>

                {/* Remaining */}
                <div
                  className={`p-3 rounded-lg ${
                    status === 'exceeded'
                      ? 'bg-red-50'
                      : status === 'warning'
                      ? 'bg-yellow-50'
                      : 'bg-green-50'
                  }`}
                >
                  <div className="flex items-center justify-between">
                    {status === 'exceeded' ? (
                      <>
                        <div className="flex items-center gap-2">
                          <AlertTriangle className="w-4 h-4 text-red-600" />
                          <span className="text-sm font-medium text-red-700">Превышен</span>
                        </div>
                        <span className="text-sm font-bold text-red-700">
                          +{formatCurrency(budget.spent - budget.limitAmount, 'KZT')}
                        </span>
                      </>
                    ) : (
                      <>
                        <span className="text-sm font-medium text-gray-700">Осталось</span>
                        <span
                          className={`text-sm font-bold ${
                            status === 'warning' ? 'text-yellow-700' : 'text-green-700'
                          }`}
                        >
                          {formatCurrency(budget.remaining, 'KZT')}
                        </span>
                      </>
                    )}
                  </div>
                </div>

                {/* Period dates */}
                <div className="mt-4 pt-4 border-t border-gray-100 text-xs text-gray-500 flex justify-between">
                  <span>
                    {new Date(budget.startDate).toLocaleDateString('ru-RU', {
                      day: '2-digit',
                      month: 'short',
                    })}
                  </span>
                  <span>—</span>
                  <span>
                    {new Date(budget.endDate).toLocaleDateString('ru-RU', {
                      day: '2-digit',
                      month: 'short',
                    })}
                  </span>
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* Budget Modal */}
      {isModalOpen && (
        <BudgetModal
          budget={editingBudget}
          categories={categories}
          onClose={() => {
            setIsModalOpen(false);
            setEditingBudget(null);
          }}
        />
      )}
    </div>
  );
}

// Budget Modal Component
function BudgetModal({
  budget,
  categories,
  onClose,
}: {
  budget: Budget | null;
  categories: Category[];
  onClose: () => void;
}) {
  const queryClient = useQueryClient();
  const [formData, setFormData] = useState<BudgetRequest>({
    categoryId: budget?.categoryId || categories[0]?.id || 0,
    limitAmount: budget?.limitAmount || 0,
    period: budget?.period || 'MONTHLY',
    startDate: budget?.startDate || new Date().toISOString().split('T')[0],
  });

  const createMutation = useMutation({
    mutationFn: budgetService.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['budgets'] });
      onClose();
    },
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, data }: { id: number; data: BudgetRequest }) =>
      budgetService.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['budgets'] });
      onClose();
    },
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (budget) {
      updateMutation.mutate({ id: budget.id, data: formData });
    } else {
      createMutation.mutate(formData);
    }
  };

  const expenseCategories = categories.filter((cat) => cat.type === 'EXPENSE');

  const handlePeriodChange = (period: BudgetPeriod) => {
    setFormData({
      ...formData,
      period,
    });
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
      <div className="bg-white rounded-xl shadow-xl max-w-md w-full">
        <div className="flex items-center justify-between p-6 border-b">
          <h2 className="text-xl font-bold text-gray-900">
            {budget ? 'Редактировать бюджет' : 'Новый бюджет'}
          </h2>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600 transition">
            <X className="w-6 h-6" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="p-6 space-y-4">
          {/* Category */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Категория расходов
            </label>
            <select
              required
              value={formData.categoryId}
              onChange={(e) => setFormData({ ...formData, categoryId: Number(e.target.value) })}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            >
              {expenseCategories.map((cat) => (
                <option key={cat.id} value={cat.id}>
                  {cat.icon} {cat.name}
                </option>
              ))}
            </select>
          </div>

          {/* Limit Amount */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Лимит расходов (₸)
            </label>
            <input
              type="number"
              step="0.01"
              required
              min="0"
              value={formData.limitAmount}
              onChange={(e) =>
                setFormData({ ...formData, limitAmount: parseFloat(e.target.value) })
              }
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              placeholder="50000.00"
            />
          </div>

          {/* Period */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Период</label>
            <div className="grid grid-cols-2 gap-2">
              {(['MONTHLY', 'YEARLY'] as BudgetPeriod[]).map((period) => (
                <button
                  key={period}
                  type="button"
                  onClick={() => handlePeriodChange(period)}
                  className={`py-2 px-3 rounded-lg font-medium transition text-sm ${
                    formData.period === period
                      ? 'bg-blue-600 text-white'
                      : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
                  }`}
                >
                  {period === 'MONTHLY' && 'Месяц'}
                  {period === 'YEARLY' && 'Год'}
                </button>
              ))}
            </div>
          </div>

          {/* Start Date */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Дата начала</label>
            <input
              type="date"
              required
              value={formData.startDate}
              onChange={(e) => setFormData({ ...formData, startDate: e.target.value })}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>

          {/* Buttons */}
          <div className="flex gap-3 pt-4">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition font-medium"
            >
              Отмена
            </button>
            <button
              type="submit"
              disabled={createMutation.isPending || updateMutation.isPending}
              className="flex-1 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition font-medium disabled:opacity-50"
            >
              {createMutation.isPending || updateMutation.isPending
                ? 'Сохранение...'
                : budget
                ? 'Сохранить'
                : 'Создать'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
