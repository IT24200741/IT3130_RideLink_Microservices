const express = require('express');
const cors = require('cors');
require('dotenv').config();

const app = express();
const PORT = process.env.ACCOUNT_SERVICE_PORT || 5001;

app.use(cors());
app.use(express.json());

// Health Check
app.get('/health', (req, res) => {
  res.status(200).json({ status: 'UP', service: 'Account Service', owner: 'Sasiru (IT24200741)', port: PORT });
});

if (process.env.NODE_ENV !== 'test') {
  app.listen(PORT, () => {
    console.log(`[Account Service] Running on port ${PORT} (Owner: Sasiru (IT24200741))`);
  });
}

module.exports = app;
