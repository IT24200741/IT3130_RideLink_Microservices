const express = require('express');
const cors = require('cors');
require('dotenv').config();

const app = express();
const PORT = process.env.DRIVER_SERVICE_PORT || 5002;

app.use(cors());
app.use(express.json());

// Health Check
app.get('/health', (req, res) => {
  res.status(200).json({ status: 'UP', service: 'Driver & Vehicle Service', owner: 'Randi Sithma (IT24104341)', port: PORT });
});

if (process.env.NODE_ENV !== 'test') {
  app.listen(PORT, () => {
    console.log(`[Driver Service] Running on port ${PORT} (Owner: Randi Sithma (IT24104341))`);
  });
}

module.exports = app;
