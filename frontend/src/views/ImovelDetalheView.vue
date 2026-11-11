
<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, RouterLink } from 'vue-router'
import { buscarImovel } from '../services/api'

const route = useRoute()

const imovel = ref(null)
const carregando = ref(true)
const erro = ref('')

async function carregarDetalhes() {
  try {
    imovel.value = await buscarImovel(route.params.id)
  } catch (erroApi) {
    erro.value = erroApi.status === 401
      ? 'É necessário estar autenticado para consultar este imóvel.'
      : 'Não foi possível carregar os detalhes do imóvel.'
  } finally {
    carregando.value = false
  }
}

function moeda(valor) {
  if (valor == null) return 'Não informado'

  return Number(valor).toLocaleString('pt-BR', {
    style: 'currency',
    currency: 'BRL'
  })
}


function contatoWhatsApp() {
  const telefone = '55996969580'

  const mensagem = `Olá! Tenho interesse no imóvel: ${imovel.value.titulo || 'Imóvel'}. Gostaria de mais informações.`

  const link = `https://wa.me/${telefone}?text=${encodeURIComponent(mensagem)}`

  window.open(link, '_blank')
}


onMounted(carregarDetalhes)
</script>

<template>
  <main class="detalhes">
    <RouterLink to="/" class="voltar">← Voltar à vitrine</RouterLink>

    <p v-if="carregando">Carregando detalhes...</p>
    <p v-else-if="erro" class="erro">{{ erro }}</p>

    <article v-else-if="imovel">
      <div class="imagem">
        {{ imovel.tipoImovel?.nome || 'Imóvel' }}
      </div>

      <h1>{{ imovel.titulo || 'Imóvel sem título' }}</h1>

      <p>{{ imovel.descricao }}</p>

      <h2>Informações do imóvel</h2>

      <p><strong>Venda:</strong> {{ moeda(imovel.precoVenda) }}</p>
      <p><strong>Aluguel:</strong> {{ moeda(imovel.precoAluguel) }}</p>
      <p><strong>Finalidade:</strong> {{ imovel.finalidade || 'Não informada' }}</p>
      <p><strong>Endereço:</strong> {{ imovel.endereco || 'Não informado' }}</p>
      <p><strong>Número:</strong> {{ imovel.numero || 'Não informado' }}</p>
      <p><strong>Bairro:</strong> {{ imovel.bairro?.nome || 'Não informado' }}</p>
      <p><strong>Cidade:</strong> {{ imovel.bairro?.cidade || 'Não informada' }}</p>
      <p><strong>Tipo:</strong> {{ imovel.tipoImovel?.nome || 'Não informado' }}</p>
      <p><strong>Dormitórios:</strong> {{ imovel.dormitorios ?? 'Não informado' }}</p>
      <p><strong>Banheiros:</strong> {{ imovel.banheiros ?? 'Não informado' }}</p>
      <p><strong>Garagem:</strong> {{ imovel.garagem ?? 'Não informado' }}</p>
      <p><strong>Área total:</strong> {{ imovel.areaTotal ?? 'Não informada' }} m²</p>
      <p><strong>Características:</strong> {{ imovel.caracteristicas || 'Não informadas' }}</p>


<div class="contato">
  <h2>Interessou-se por este imóvel?</h2>

  <p>Entre em contato com nossa imobiliária para obter mais informações.</p>

  <button @click="contatoWhatsApp">
    Falar pelo WhatsApp
  </button>
</div>

    </article>
  </main>
</template>

<style scoped>

.detalhes {
  width: 100%;
  max-width: 1000px;
  margin: 35px auto;
  padding: 25px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 18px #173b5e12;
}

.voltar {
  display: inline-block;
  margin: 5px 0 25px;
  color: #285b87;
  font-weight: bold;
}

.voltar:hover {
  color: #173b5e;
  text-decoration: underline;
}

.imagem {
  height: 280px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #dce7f0, #c7d7e5);
  color: #244b70;
  font-size: 28px;
  font-weight: bold;
  border-radius: 10px;
  margin-bottom: 25px;
}

h1 {
  color: #173b5e;
  font-size: 30px;
  font-weight: bold;
  margin-bottom: 12px;
}

h2 {
  color: #173b5e;
  font-size: 21px;
  font-weight: bold;
  margin: 25px 0 15px;
}

article {
  padding-bottom: 30px;
}

article > p {
  color: #536477;
  padding: 9px 0;
  border-bottom: 1px solid #edf0f3;
}

article > p strong {
  color: #244b70;
}

.erro {
  color: #b42318;
  padding: 20px 0;
}

.contato {
  margin-top: 35px;
  padding: 28px;
  background: #f1f6f9;
  border: 1px solid #e0e8ef;
  border-radius: 10px;
}

.contato h2 {
  margin-top: 0;
}

.contato p {
  color: #536477;
  margin-bottom: 18px;
}

.contato button {
  padding: 13px 23px;
  background: #25d366;
  color: white;
  border: none;
  border-radius: 7px;
  font-size: 15px;
  font-weight: bold;
  cursor: pointer;
  transition: background 0.2s;
}

.contato button:hover {
  background: #1da851;
}

@media (max-width: 600px) {
  .detalhes {
    margin: 15px auto;
    padding: 18px;
  }

  .imagem {
    height: 190px;
  }

  h1 {
    font-size: 24px;
  }

  .contato {
    padding: 20px;
  }
}

</style>
