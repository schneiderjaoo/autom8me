import express from "express";
import { getAIResponse } from "./src/services/ServiceGemini.js";

const app = express();
const port = 3000;

app.get('/', async (req, res) => {
  try {
    const response = await getAIResponse("Poderia me dar um resumo sobre o que é pipeline");
    res.send(response);
  } catch (err) {
    console.error(err);
    res.status(500).send("Erro ao gerar conteúdo com IA.");
  }
});

app.listen(port, () => {
  console.log(`Exemplo de app rodando em http://localhost:${port}`);
});
