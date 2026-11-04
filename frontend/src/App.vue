<script setup>
import { ref, onMounted } from 'vue'

// Lista que receberá os imóveis da API
const imoveis = ref([])

// Controla o carregamento e possíveis erros
const carregando = ref(true)
const erro = ref('')

// Busca os imóveis no backend
async function carregarImoveis() {
  try {
    const resposta = await fetch('/imoveis')

    if (!resposta.ok) {
      throw new Error('Erro ao buscar os imóveis.')
    }

    imoveis.value = await resposta.json()
  } catch (erroResposta) {
    console.error(erroResposta)
    erro.value = 'Não foi possível carregar os imóveis. Verifique se o backend está funcionando.'
  } finally {
    carregando.value = false
  }
}

// Formata valores como moeda brasileira
function formatarPreco(valor) {
  if (valor == null) {
    return 'A consultar'
  }

  return Number(valor).toLocaleString('pt-BR', {
    style: 'currency',
    currency: 'BRL'
  })
}

// Executa a busca quando a página abrir
onMounted(() => {
  carregarImoveis()
})
</script>

<template>
  <main class="pagina">
    <header class="cabecalho">
      <h1>Imobiliária</h1>
      <p>Encontre o imóvel ideal para você</p>
    </header>

    <section class="conteudo">
      <h2>Imóveis disponíveis</h2>

      <p v-if="carregando" class="mensagem">
        Carregando imóveis...
      </p>

      <p v-else-if="erro" class="erro">
        {{ erro }}
      </p>

      <p v-else-if="imoveis.length === 0" class="mensagem">
        Nenhum imóvel cadastrado.
      </p>

      <div v-else class="lista-imoveis">
        <article
          v-for="imovel in imoveis"
          :key="imovel.id"
          class="card-imovel"
        >
          <div class="card-conteudo">
            <h3>{{ imovel.titulo }}</h3>

            <p class="preco">
              {{ formatarPreco(imovel.precoVenda ?? imovel.precoAluguel) }}
            </p>

            <p>
              <strong>Finalidade:</strong>
              {{ imovel.finalidade || 'Não informada' }}
            </p>

            <p>
              <strong>Endereço:</strong>
              {{ imovel.endereco || 'Não informado' }}
            </p>

            <p v-if="imovel.bairro">
              <strong>Bairro:</strong>
              {{ imovel.bairro.nome }}
            </p>

            <p>
              <strong>Dormitórios:</strong>
              {{ imovel.dormitorios ?? 0 }}
            </p>

            <p>
              <strong>Banheiros:</strong>
              {{ imovel.banheiros ?? 0 }}
            </p>

            <p>
              <strong>Garagem:</strong>
              {{ imovel.garagem ?? 0 }}
            </p>

            <p>
              <strong>Área total:</strong>
              {{ imovel.areaTotal ?? 0 }} m²
            </p>

            <p>
              <strong>Status:</strong>
              {{ imovel.status || 'Não informado' }}
            </p>
          </div>
        </article>
      </div>
    </section>
  </main>
</template>

<style>
* {
  box-sizing: border-box;
}

body {
  margin: 0;
  font-family: Arial, sans-serif;
  background: #f4f5f7;
  color: #333;
}

.pagina {
  min-height: 100vh;
}

.cabecalho {
  background: #244b70;
  color: white;
  padding: 30px;
  text-align: center;
}

.cabecalho h1 {
  margin: 0;
}

.conteudo {
  max-width: 1100px;
  margin: 30px auto;
  padding: 0 20px;
}

.lista-imoveis {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 20px;
}

.card-imovel {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.card-imovel h3 {
  margin-top: 0;
  color: #244b70;
}

.preco {
  color: #16834a;
  font-size: 20px;
  font-weight: bold;
}

.mensagem {
  padding: 20px;
}

.erro {
  color: #c62828;
  padding: 20px;
}
</style>