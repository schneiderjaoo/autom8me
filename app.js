import express from "express";
import fs from "fs";
import path from "path";
import { getAIResponse } from "./src/services/ServiceGemini.js";

const app = express();
const port = 3000;

app.get("/", async (req, res) => {
  try {
    // Lê o arquivo de logs
    const logsPath = path.join(process.cwd(), "logs", "logs.log");
    const logsContent = fs.readFileSync(logsPath, "utf8");
    
    // Prompt específico para processar os logs
    const prompt = `Analise os seguintes logs de commits e gere release notes organizadas:\n\n${logsContent}`;
    
    // Envia para o Gemini
    const response = await getAIResponse(prompt);
    res.send(response);
  } catch (err) {
    console.error("Erro ao processar logs:", err.message);
    res.status(500).send("Erro ao processar arquivo de logs.");
  }
});

app.listen(port, () => {
  console.log(`Servidor rodando em http://localhost:${port}`);
});
