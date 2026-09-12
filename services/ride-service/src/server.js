const express = require('express');
const cors = require('cors');
require('dotenv').config();

const app = express();
const PORT = process.env.RIDE_SERVICE_PORT || 5003;

app.use(cors());
app.use(express.json());

// Health Check
app.get('/health', (req, res) => {
  res.status(200).json({ status: 'UP', service: 'Ride Management Service', owner: 'Bhanuka', port: PORT });
});

if (process.env.NODE_ENV !== 'test') {
  app.listen(PORT, () => {
    console.log(`[Ride Service] Running on port ${PORT} (Owner: Bhanuka)`);
  });
}

module.exports = app;
