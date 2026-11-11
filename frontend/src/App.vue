
<template>
  <header class="cabecalho">
    <div class="container menu">
      <RouterLink to="/" class="logo">Imobiliária</RouterLink>

      <nav>
        <RouterLink to="/">Vitrine</RouterLink>

        <RouterLink v-if="!logado" to="/login">
          Entrar
        </RouterLink>

        <template v-else>
          <RouterLink v-if="perfil === 'ROLE_ADMIN'" to="/admin">
            Painel Administrativo
          </RouterLink>

          <RouterLink v-else to="/cliente">
            Minha Área
          </RouterLink>

          <button @click="sair">Sair</button>
        </template>
      </nav>
    </div>
  </header>

  <main>
    <RouterView />
  </main>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRoute, useRouter, RouterLink, RouterView } from 'vue-router'
import { estaLogado, obterPerfil, realizarLogout } from './services/auth'

const router = useRouter()
const route = useRoute()

const logado = ref(estaLogado())
const perfil = ref(obterPerfil())

function atualizarMenu() {
  logado.value = estaLogado()
  perfil.value = obterPerfil()
}

function sair() {
  realizarLogout()
  atualizarMenu()
  router.push('/')
}

// Atualiza o menu quando a página muda
watch(() => route.path, atualizarMenu)
</script>
