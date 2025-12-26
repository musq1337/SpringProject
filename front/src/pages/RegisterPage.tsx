import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { authService } from '../services/auth.service';
import type { RegisterRequest } from '../types';
import { Currency } from '../types';

export default function RegisterPage() {
  const [error, setError] = useState<string>('');
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const navigate = useNavigate();
  
  const {
    register,
    handleSubmit,
    formState: { errors },
    watch,
  } = useForm<RegisterRequest & { confirmPassword: string }>();

  const password = watch('password');

  const onSubmit = async (data: RegisterRequest & { confirmPassword: string }) => {
    try {
      setLoading(true);
      setError('');
      await authService.register(data);
      navigate('/login');
    } catch (err) {
      setError((err as any).response?.data?.message || 'Ошибка регистрации');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center p-6">
      <div className="w-full max-w-md">
        <div className="bg-white rounded-3xl shadow-2xl p-8">
          <h1 className="text-4xl font-bold text-gray-800 mb-8">Создать аккаунт</h1>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          {error && (
            <div className="bg-red-50 text-red-600 p-3 rounded-xl text-sm border border-red-200">
              {error}
            </div>
          )}

          <div className="grid grid-cols-2 gap-3">
            <div>
              <input
                {...register('firstName', { required: 'Имя обязательно' })}
                type="text"
                placeholder="Имя"
                className="w-full px-4 py-3 bg-gray-100 border-2 border-gray-200 rounded-2xl focus:border-purple-500 focus:bg-white outline-none transition text-gray-800 placeholder-gray-500"
              />
              {errors.firstName && (
                <p className="text-red-600 text-xs mt-1 ml-2">{errors.firstName.message}</p>
              )}
            </div>

            <div>
              <input
                {...register('lastName', { required: 'Фамилия обязательна' })}
                type="text"
                placeholder="Фамилия"
                className="w-full px-4 py-3 bg-gray-100 border-2 border-gray-200 rounded-2xl focus:border-purple-500 focus:bg-white outline-none transition text-gray-800 placeholder-gray-500"
              />
              {errors.lastName && (
                <p className="text-red-600 text-xs mt-1 ml-2">{errors.lastName.message}</p>
              )}
            </div>
          </div>

          <div>
            <input
              {...register('email', {
                required: 'Email обязателен',
                pattern: {
                  value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                  message: 'Неверный формат email',
                },
              })}
              type="email"
              placeholder="Email"
              className="w-full px-4 py-3 bg-gray-100 border-2 border-gray-200 rounded-2xl focus:border-purple-500 focus:bg-white outline-none transition text-gray-800 placeholder-gray-500"
            />
            {errors.email && (
              <p className="text-red-600 text-xs mt-1 ml-2">{errors.email.message}</p>
            )}
          </div>

          <div>
            <select
              {...register('defaultCurrency', { required: 'Выберите валюту' })}
              className="w-full px-4 py-3 bg-gray-100 border-2 border-gray-200 rounded-2xl focus:border-purple-500 focus:bg-white outline-none transition cursor-pointer text-gray-800"
            >
              <option value={Currency.KZT}>₸ Тенге</option>
              <option value={Currency.USD}>$ Доллар</option>
              <option value={Currency.EUR}>€ Евро</option>
            </select>
          </div>

          <div className="relative">
            <input
              {...register('password', {
                required: 'Пароль обязателен',
                minLength: {
                  value: 6,
                  message: 'Минимум 6 символов',
                },
              })}
              type={showPassword ? 'text' : 'password'}
              placeholder="Пароль"
              className="w-full px-4 py-3 pr-12 bg-gray-100 border-2 border-gray-200 rounded-2xl focus:border-purple-500 focus:bg-white outline-none transition text-gray-800 placeholder-gray-500"
            />
            <button
              type="button"
              onClick={() => setShowPassword(!showPassword)}
              className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-700 transition"
            >
              {showPassword ? (
                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21" />
                </svg>
              ) : (
                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                </svg>
              )}
            </button>
            {errors.password && (
              <p className="text-red-600 text-xs mt-1 ml-2">{errors.password.message}</p>
            )}
          </div>

          <div className="relative">
            <input
              {...register('confirmPassword', {
                required: 'Подтвердите пароль',
                validate: (value) =>
                  value === password || 'Пароли не совпадают',
              })}
              type={showConfirmPassword ? 'text' : 'password'}
              placeholder="Подтвердите пароль"
              className="w-full px-4 py-3 pr-12 bg-gray-100 border-2 border-gray-200 rounded-2xl focus:border-purple-500 focus:bg-white outline-none transition text-gray-800 placeholder-gray-500"
            />
            <button
              type="button"
              onClick={() => setShowConfirmPassword(!showConfirmPassword)}
              className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-700 transition"
            >
              {showConfirmPassword ? (
                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21" />
                </svg>
              ) : (
                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                </svg>
              )}
            </button>
            {errors.confirmPassword && (
              <p className="text-red-600 text-xs mt-1 ml-2">{errors.confirmPassword.message}</p>
            )}
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-gradient-to-r from-purple-600 to-indigo-600 hover:from-purple-700 hover:to-indigo-700 text-white font-bold py-3 px-4 rounded-2xl transition shadow-lg hover:shadow-xl disabled:opacity-50 mt-6"
          >
            {loading ? 'Регистрация...' : 'Создать аккаунт'}
          </button>

          <div className="text-center mt-4">
            <button
              type="button"
              onClick={() => navigate('/login')}
              className="text-purple-600 hover:text-purple-700 font-medium text-sm"
            >
              Уже есть аккаунт? <span className="font-bold">Войти</span>
            </button>
          </div>
        </form>
        </div>
      </div>
    </div>
  );
}
