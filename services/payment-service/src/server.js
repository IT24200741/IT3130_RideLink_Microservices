const express = require('express');
const cors = require('cors');
require('dotenv').config();

const app = express();
const PORT = process.env.PAYMENT_SERVICE_PORT || 5004;

app.use(cors());
app.use(express.json());

// Health Check
app.get('/health', (req, res) => {
  res.status(200).json({ status: 'UP', service: 'Fare & Payment Service', owner: 'Nethmini Perera', port: PORT });
});

if (process.env.NODE_ENV !== 'test') {
  app.listen(PORT, () => {
    console.log(`[Payment Service] Running on port ${PORT} (Owner: Nethmini Perera)`);
  });
}

module.exports = app;
