import express from "express";
import { getAIResponse } from "./src/services/ServiceGemini.js";

const app = express();
const port = 3000;

app.get("/", async (req, res) => {
  const { prompt } = req.query;

  if (!prompt) {
    return res.status(400).send("Parâmetro 'prompt' é obrigatório.");
  }

  try {
    const response = await getAIResponse(prompt);
    res.send(response);
  } catch (err) {
    console.error("Erro ao gerar resposta da IA:", err.message);
    res.status(500).send("Erro ao gerar conteúdo com IA.");
  }
});

app.listen(port, () => {
  console.log(`Servidor rodando em http://localhost:${port}`);
});
