const bcrypt = require('bcryptjs');
const pwd = process.argv[2] || 'owner';
const saltRounds = 10;
const hash = bcrypt.hashSync(pwd, saltRounds);
console.log(hash);
