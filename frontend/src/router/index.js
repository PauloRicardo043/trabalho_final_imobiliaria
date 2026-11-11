
import { createRouter, createWebHistory } from 'vue-router'

import VitrineView from '../views/VitrineView.vue'
import ImovelDetalheView from '../views/ImovelDetalheView.vue'
import LoginView from '../views/LoginView.vue'
import AdminView from '../views/AdminView.vue'
import ClienteView from '../views/ClienteView.vue'
import { obterToken, obterPerfil } from '../services/auth'

const routes = [
  {
    path: '/',
    component: VitrineView
  },
  {
    path: '/imovel/:id',
    component: ImovelDetalheView
  },
  {
    path: '/login',
    component: LoginView
  },
  {
    path: '/admin',
    component: AdminView,
    meta: { perfil: 'ROLE_ADMIN' }
  },
  {
    path: '/cliente',
    component: ClienteView,
    meta: { perfil: 'ROLE_USER' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})


router.beforeEach((to) => {
  const token = obterToken()
  const perfil = obterPerfil()

  // Se a rota exige login e não existe token, vai para o login
  if (to.meta.perfil && !token) {
    return '/login'
  }

  // Se o usuário não tem o perfil necessário, redireciona
  if (to.meta.perfil && perfil !== to.meta.perfil) {
    if (perfil === 'ROLE_ADMIN') {
      return '/admin'
    }

    if (perfil === 'ROLE_USER') {
      return '/cliente'
    }

    return '/login'
  }

  // Se já está logado e tenta acessar o login
  if (to.path === '/login' && token) {
    return perfil === 'ROLE_ADMIN' ? '/admin' : '/cliente'
  }
})

export default router
