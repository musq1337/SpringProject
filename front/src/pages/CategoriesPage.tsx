import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Plus, Edit2, Trash2, X, FolderOpen, TrendingUp, TrendingDown } from 'lucide-react';
import { categoryService } from '../services/category.service';
import type { Category, CategoryRequest, TransactionType } from '../types';
import { getCategoryColor } from '../utils/colors';

export default function CategoriesPage() {
  const queryClient = useQueryClient();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingCategory, setEditingCategory] = useState<Category | null>(null);
  const [filterType, setFilterType] = useState<TransactionType | 'ALL'>('ALL');

  const { data: categories = [], isLoading } = useQuery({
    queryKey: ['categories'],
    queryFn: () => categoryService.getAll(),
  });

  const deleteMutation = useMutation({
    mutationFn: categoryService.delete,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['categories'] });
      queryClient.invalidateQueries({ queryKey: ['transactions'] });
      queryClient.invalidateQueries({ queryKey: ['dashboard'] });
    },
  });

  const handleEdit = (category: Category) => {
    setEditingCategory(category);
    setIsModalOpen(true);
  };

  const handleDelete = (category: Category) => {
    if (category.isDefault) {
      alert('Нельзя удалить категорию по умолчанию');
      return;
    }
    if (window.confirm(`Вы уверены, что хотите удалить категорию "${category.name}"?`)) {
      deleteMutation.mutate(category.id);
    }
  };

  const handleAddNew = () => {
    setEditingCategory(null);
    setIsModalOpen(true);
  };

  const filteredCategories = categories.filter((cat) =>
    filterType === 'ALL' ? true : cat.type === filterType
  );

  const expenseCategories = filteredCategories.filter((cat) => cat.type === 'EXPENSE');
  const incomeCategories = filteredCategories.filter((cat) => cat.type === 'INCOME');

  return (
    <div className="p-6 max-w-7xl mx-auto">
      {/* Header */}
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Категории</h1>
          <p className="text-gray-600 mt-1">Управление категориями доходов и расходов</p>
        </div>
        <button
          onClick={handleAddNew}
          className="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition"
        >
          <Plus className="w-5 h-5" />
          Добавить категорию
        </button>
      </div>

      {/* Filter Tabs */}
      <div className="flex gap-2 mb-6">
        <button
          onClick={() => setFilterType('ALL')}
          className={`px-4 py-2 rounded-lg font-medium transition ${
            filterType === 'ALL'
              ? 'bg-blue-600 text-white'
              : 'bg-white text-gray-700 border border-gray-300 hover:bg-gray-50'
          }`}
        >
          Все ({categories.length})
        </button>
        <button
          onClick={() => setFilterType('EXPENSE')}
          className={`px-4 py-2 rounded-lg font-medium transition flex items-center gap-2 ${
            filterType === 'EXPENSE'
              ? 'bg-red-600 text-white'
              : 'bg-white text-gray-700 border border-gray-300 hover:bg-gray-50'
          }`}
        >
          <TrendingDown className="w-4 h-4" />
          Расходы ({expenseCategories.length})
        </button>
        <button
          onClick={() => setFilterType('INCOME')}
          className={`px-4 py-2 rounded-lg font-medium transition flex items-center gap-2 ${
            filterType === 'INCOME'
              ? 'bg-green-600 text-white'
              : 'bg-white text-gray-700 border border-gray-300 hover:bg-gray-50'
          }`}
        >
          <TrendingUp className="w-4 h-4" />
          Доходы ({incomeCategories.length})
        </button>
      </div>

      {isLoading ? (
        <div className="text-center py-12">
          <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
          <p className="text-gray-500 mt-2">Загрузка...</p>
        </div>
      ) : (
        <div className="space-y-6">
          {/* Expense Categories */}
          {(filterType === 'ALL' || filterType === 'EXPENSE') && expenseCategories.length > 0 && (
            <div>
              <h2 className="text-xl font-semibold text-gray-900 mb-4 flex items-center gap-2">
                <TrendingDown className="w-5 h-5 text-red-600" />
                Расходы
              </h2>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
                {expenseCategories.map((category) => (
                  <CategoryCard
                    key={category.id}
                    category={category}
                    onEdit={handleEdit}
                    onDelete={handleDelete}
                  />
                ))}
              </div>
            </div>
          )}

          {/* Income Categories */}
          {(filterType === 'ALL' || filterType === 'INCOME') && incomeCategories.length > 0 && (
            <div>
              <h2 className="text-xl font-semibold text-gray-900 mb-4 flex items-center gap-2">
                <TrendingUp className="w-5 h-5 text-green-600" />
                Доходы
              </h2>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
                {incomeCategories.map((category) => (
                  <CategoryCard
                    key={category.id}
                    category={category}
                    onEdit={handleEdit}
                    onDelete={handleDelete}
                  />
                ))}
              </div>
            </div>
          )}

          {/* Empty State */}
          {filteredCategories.length === 0 && (
            <div className="text-center py-12 bg-white rounded-lg border border-gray-200">
              <FolderOpen className="w-16 h-16 text-gray-400 mx-auto mb-4" />
              <p className="text-gray-500 text-lg mb-2">Нет категорий</p>
              <p className="text-gray-400 text-sm">Добавьте первую категорию для начала работы</p>
            </div>
          )}
        </div>
      )}

      {/* Category Modal */}
      {isModalOpen && (
        <CategoryModal
          category={editingCategory}
          onClose={() => {
            setIsModalOpen(false);
            setEditingCategory(null);
          }}
        />
      )}
    </div>
  );
}

// Category Card Component
function CategoryCard({
  category,
  onEdit,
  onDelete,
}: {
  category: Category;
  onEdit: (category: Category) => void;
  onDelete: (category: Category) => void;
}) {
  const color = getCategoryColor(category.name);

  return (
    <div
      className="bg-white rounded-lg border border-gray-200 p-4 hover:shadow-md transition group"
      style={{ borderLeftWidth: '4px', borderLeftColor: color }}
    >
      <div className="flex items-start justify-between mb-3">
        <div className="flex items-center gap-3">
          <div
            className="w-12 h-12 rounded-lg flex items-center justify-center text-2xl"
            style={{ backgroundColor: `${color}20` }}
          >
            {category.icon || '📁'}
          </div>
          <div>
            <h3 className="font-semibold text-gray-900">{category.name}</h3>
            {category.isDefault && (
              <span className="text-xs text-gray-500 bg-gray-100 px-2 py-0.5 rounded">
                По умолчанию
              </span>
            )}
          </div>
        </div>
      </div>

      <div className="flex items-center justify-between mt-4 pt-3 border-t border-gray-100">
        <span
          className={`text-xs font-medium px-2 py-1 rounded ${
            category.type === 'INCOME'
              ? 'bg-green-100 text-green-700'
              : 'bg-red-100 text-red-700'
          }`}
        >
          {category.type === 'INCOME' ? 'Доход' : 'Расход'}
        </span>
        <div className="flex gap-1 opacity-0 group-hover:opacity-100 transition">
          <button
            onClick={() => onEdit(category)}
            className="p-1.5 text-blue-600 hover:bg-blue-50 rounded transition"
            title="Редактировать"
          >
            <Edit2 className="w-4 h-4" />
          </button>
          {!category.isDefault && (
            <button
              onClick={() => onDelete(category)}
              className="p-1.5 text-red-600 hover:bg-red-50 rounded transition"
              title="Удалить"
            >
              <Trash2 className="w-4 h-4" />
            </button>
          )}
        </div>
      </div>
    </div>
  );
}

// Category Modal Component
function CategoryModal({
  category,
  onClose,
}: {
  category: Category | null;
  onClose: () => void;
}) {
  const queryClient = useQueryClient();
  const [formData, setFormData] = useState<CategoryRequest>({
    name: category?.name || '',
    type: category?.type || 'EXPENSE',
    icon: category?.icon || '📁',
    color: category?.color,
  });

  const createMutation = useMutation({
    mutationFn: categoryService.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['categories'] });
      onClose();
    },
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, data }: { id: number; data: CategoryRequest }) =>
      categoryService.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['categories'] });
      onClose();
    },
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (category) {
      updateMutation.mutate({ id: category.id, data: formData });
    } else {
      createMutation.mutate(formData);
    }
  };

  const commonIcons = [
    '🍔', '🚗', '🏠', '💊', '🎓', '🎬',
    '👕', '⚡', '📱', '🎮', '🏋️', '✈️',
    '💼', '💰', '🎁', '📊', '🔧', '🌟',
  ];

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
      <div className="bg-white rounded-xl shadow-xl max-w-md w-full">
        <div className="flex items-center justify-between p-6 border-b">
          <h2 className="text-xl font-bold text-gray-900">
            {category ? 'Редактировать категорию' : 'Новая категория'}
          </h2>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600 transition">
            <X className="w-6 h-6" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="p-6 space-y-4">
          {/* Type */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Тип</label>
            <div className="grid grid-cols-2 gap-2">
              <button
                type="button"
                onClick={() => setFormData({ ...formData, type: 'EXPENSE' })}
                className={`py-2 px-4 rounded-lg font-medium transition ${
                  formData.type === 'EXPENSE'
                    ? 'bg-red-100 text-red-700 border-2 border-red-300'
                    : 'bg-gray-100 text-gray-600 border-2 border-transparent'
                }`}
              >
                Расход
              </button>
              <button
                type="button"
                onClick={() => setFormData({ ...formData, type: 'INCOME' })}
                className={`py-2 px-4 rounded-lg font-medium transition ${
                  formData.type === 'INCOME'
                    ? 'bg-green-100 text-green-700 border-2 border-green-300'
                    : 'bg-gray-100 text-gray-600 border-2 border-transparent'
                }`}
              >
                Доход
              </button>
            </div>
          </div>

          {/* Name */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Название</label>
            <input
              type="text"
              required
              value={formData.name}
              onChange={(e) => setFormData({ ...formData, name: e.target.value })}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              placeholder="Например: Продукты"
            />
          </div>

          {/* Icon Picker */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Иконка (выбранная: {formData.icon})
            </label>
            <div className="grid grid-cols-9 gap-2 p-3 bg-gray-50 rounded-lg border border-gray-200 max-h-40 overflow-y-auto">
              {commonIcons.map((icon) => (
                <button
                  key={icon}
                  type="button"
                  onClick={() => setFormData({ ...formData, icon })}
                  className={`text-2xl p-2 rounded hover:bg-white transition ${
                    formData.icon === icon ? 'bg-blue-100 ring-2 ring-blue-500' : 'bg-white'
                  }`}
                >
                  {icon}
                </button>
              ))}
            </div>
            <input
              type="text"
              value={formData.icon}
              onChange={(e) => setFormData({ ...formData, icon: e.target.value })}
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent mt-2"
              placeholder="Или введите свою иконку"
              maxLength={2}
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
                : category
                ? 'Сохранить'
                : 'Добавить'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
