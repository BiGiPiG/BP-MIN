import { createRouter, createWebHistory } from 'vue-router'
import LoginPage from "@/components/LoginPage.vue";
import MainPage from "@/components/MainPage.vue";
import { isTokenExpired } from "@/utils/jwtUtils.js";
import { useStomp } from "@/utils/useStomp.js";
import { authApi } from "@/api";

const {
  connect
} = useStomp()

const router = createRouter({
  routes: [{
    path: '/login',
    name: 'Login',
    component: LoginPage,
    meta: {
      requiresAuth: false
    }
  },
    {
      path: '/bp-min/:username',
      name: 'BpMin', component: MainPage,
      meta: {
        requiresAuth: true
      }
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/login'
    },
    {
      path: '/bp-min/:username/profile',
      name: 'Profile',
      component: () => import('@/components/UserProfile.vue'),
      meta: {
        requiresAuth: true
      }
    }
  ],
  history: createWebHistory()
})

router.beforeEach(async (to, from, next) => {
  const requiresAuth = to.meta?.requiresAuth === true;

  const accessToken = localStorage.getItem('accessToken');
  const refreshToken = localStorage.getItem('refreshToken');

  if (!requiresAuth) {
    return next();
  }

  if (!accessToken || !refreshToken) {
    return next({ name: 'Login' });
  }

  if (!isTokenExpired(accessToken)) {
    return next();
  }

  try {
    const { accessToken: newAccessToken, refreshToken: newRefreshToken } =
      await authApi.refreshTokens(refreshToken);

    localStorage.setItem('accessToken', newAccessToken);
    localStorage.setItem('refreshToken', newRefreshToken);
  } catch (error) {
    console.error('Refresh failed:', error);
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    return next({ name: 'Login' });
  }

  next();
});

export default router
