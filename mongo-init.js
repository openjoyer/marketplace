db = db.getSiblingDB('admin');

// Администрирование
db.createUser({
    user: "admin",
    pwd: "admin",
    roles: [ { role: "userAdminAnyDatabase", db: "admin" } ]
});

db = db.getSiblingDB('marketplace');
db.createUser({
    user: "openjoyer",
    pwd: "1234",
    roles: [ { role: "readWrite", db: "marketplace" } ]
});

// Создаем коллекции
db.createCollection('profiles');
db.profiles.createIndex({ email: 1 }, { unique: true });
db.profiles.createIndex({ id: 1 }, {unique: true})

db.createCollection('products');
db.products.createIndex({ id: 1 }, {unique: true})

db.createCollection('notifications')
db.notifications.createIndex({ id: 1 }, {unique: true})
