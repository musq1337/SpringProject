export const colors = {
  primary: '#3b82f6',
  secondary: '#6366f1',
  success: '#10b981',
  danger: '#ef4444',
  warning: '#f59e0b',
  info: '#06b6d4',
  
  income: '#10b981',
  expense: '#ef4444',
  
  categoryColors: [
    '#3b82f6', '#8b5cf6', '#ec4899', '#f43f5e',
    '#f97316', '#f59e0b', '#84cc16', '#10b981',
    '#14b8a6', '#06b6d4', '#0ea5e9', '#6366f1',
  ],
};

export const getCategoryColor = (index: number): string => {
  return colors.categoryColors[index % colors.categoryColors.length];
};
