const mysql = require('mysql2/promise');

async function run() {
  const conn = await mysql.createConnection({
    host: 'localhost',
    user: 'root',
    password: 'Password123@#$',
    database: 'bakery'
  });

  try {
    const [result] = await conn.execute('UPDATE user SET approved = 1');
    console.log('Successfully approved existing users. Affected rows:', result.affectedRows);
  } catch (err) {
    console.error('Error running update:', err.message || err);
  } finally {
    await conn.end();
  }
}

run();
