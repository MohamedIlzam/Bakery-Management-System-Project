const mysql = require('mysql2/promise');

async function run() {
  const conn = await mysql.createConnection({
    host: 'localhost',
    user: 'root',
    password: 'Password123@#$',
    database: 'bakery'
  });

  const sql = `INSERT INTO user (user_id, password, role, username) VALUES (?, ?, ?, ?)`;
  const params = ['USR001', '$2b$10$nBQ8N3YmOcJo//gYe5/UoeJvZWfZcGu8AfnYBoGO7wjcGGMvmdnom', 'ROLE_OWNER', 'owner'];

  const [result] = await conn.execute(sql, params);
  console.log('Insert result:', result.affectedRows);
  await conn.end();
}

run().catch(err => { console.error('Error inserting owner:', err.message || err); process.exit(1); });
