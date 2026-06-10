const mysql = require('mysql2/promise');

async function run() {
  const conn = await mysql.createConnection({
    host: 'localhost',
    user: 'root',
    password: 'Password123@#$',
    database: 'bakery'
  });

  try {
    const [rows] = await conn.execute('SELECT user_id, username, role, approved, password FROM user');
    console.log('--- USER TABLE CONTENTS ---');
    console.log(JSON.stringify(rows, null, 2));
    console.log('---------------------------');
  } catch (err) {
    console.error('Error running select:', err.message || err);
  } finally {
    await conn.end();
  }
}

run();
