create database SectorSalud;
use SectorSalud;


create table ciudadanos(
	id_ciudadano int not null auto_increment,
    nombre varchar(100) not null,
    apellido varchar(100) not null,
    curp varchar(18) unique not null,
    fecha_nacimiento date not null,
    genero enum ('Masculino', 'Femenino') not null, 
    telefono varchar(15) not null, 
    calle varchar(50) not null,
    numero_exterior varchar(10) not null,
    numero_interior varchar(10),
    colonia varchar(50) not null,
    municipio varchar(50) not null,
    estado varchar(50) not null, 
    codigo_postal varchar(5) not null,
    constraint PK_ciudadanos primary key(id_ciudadano)
);
-- Cuidadanos
insert into ciudadanos (nombre, apellido, curp, fecha_nacimiento, genero, telefono, calle, numero_exterior, numero_interior, colonia, municipio, estado, codigo_postal) values 
("Teresa Margarita", "Zavala Cantu", "ZACT041217MNLVNRA2", "2004-12-17", "Femenino", "8116662768", "Musas", "310",null, "Arcadia", "Juarez", "Nuevo Leon", "67286");
select*from ciudadanos;

create table usuarios_ciudadanos(
	correo varchar(100) not null,
    password_ varchar(50) not null,
    id_ciudadano int not null,
	constraint FK_usuarios_ciudadanos foreign key(id_ciudadano) references ciudadanos (id_ciudadano)
);
-- Usuarios Cuidadanos
insert into usuarios_ciudadanos (correo, password_, id_ciudadano) values
("teresazavala75@gmail.com", "Kdrama654", 1);
select*from usuarios_ciudadanos;

create table esavi(
	id_reporte int not null auto_increment,
    curp_cuidadano varchar(18) not null,
    nombre_cuidadano varchar(100) not null,
    cedula_doctor varchar(8) not null,
    id_lote int not null,
    id_centro int not null,
    descripcion varchar(150) not null,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    constraint PK_esavi primary key(id_reporte),
    constraint FK_esavi_curp foreign key(curp_cuidadano) references ciudadanos(curp),
    constraint FK_esavi_cedula foreign key(cedula_doctor) references empleados(cedula_profesional),
    constraint FK_esavi_centro foreign key(id_centro) references centros_salud(id_centro),
    constraint FK_esavi_lote foreign key(id_lote) references lotes(id_lote)
);

create table roles(
	id_rol int,
    nombre varchar(50) unique not null,
    constraint PK_roles primary key(id_rol)
);
-- Roles
insert into roles (id_rol, nombre) values
(1, "Médico"),
(2, "Enfermero(a)"),
(3, "Personal Administrativo");
select*from roles;

create table centros_salud(
	id_centro int not null auto_increment,
    nombre varchar(100) unique not null, 
    calle varchar(50) not null,
    numero_exterior varchar(10) not null,
    numero_interior varchar(10),
    colonia varchar(50) not null,
    municipio varchar(50) not null,
    estado varchar(50) not null, 
    codigo_postal varchar(5) not null,
    telefono varchar(20) not null, 
    horario_apertura time not null,
    horario_cierre time not null,
    activo boolean default true, 
    constraint PK_centro_salud primary key(id_centro)
);

-- CENTROS DE SALUD
INSERT INTO centros_salud (id_centro, nombre, calle, numero_exterior, numero_interior, colonia, municipio, estado, codigo_postal, telefono, horario_apertura, horario_cierre, activo) VALUES
(100, 'Hospital Metropolitano Bernardo Sepúlveda', 'Av. Adolfo López Mateos', 's/n', NULL, 'San Nicolás de los Garza', 'San Nicolás de los Garza', 'Nuevo León', '66450', '8183056000', '00:00:00', '23:59:59', true),
(200, 'Centro de Salud T-III Dr. José Zozaya', 'Calle Corregidora', 's/n', NULL, 'Santa Anita', 'Iztacalco', 'Ciudad de México', '08300', '5555303262', '07:00:00', '20:00:00', true),
(300, 'Centro de Salud Número 1 Cancún', 'Av. Bonampak', 's/n', NULL, 'SM 68 Mza 6', 'Cancún', 'Quintana Roo', '77500', '9988842666', '07:30:00', '19:30:00', true);

create table empleados(
	id_empleado int,
    nombre varchar(100) not null,
    apellido varchar(100) not null,
    curp varchar(18) unique not null,
    cedula_profesional varchar(8) unique not null,
    id_rol int not null,
    id_centro int,
    constraint PK_empleados primary key(id_empleado),
    constraint FK_roles foreign key(id_rol) references roles(id_rol),
	constraint FK_centros_salud foreign key(id_centro) references centros_salud(id_centro)
);
-- EMPLEADOS
insert into empleados (id_empleado, nombre, apellido, curp, cedula_profesional, id_rol, id_centro) values 
(1, "Teresa Margarita", "Zavala Cantu", "ZACT041217MNLVNRA2", "2025218", 3, 100),
(2, "David Alejandro", "Garcia Barboza", "GABD040312HNLVYFK3", "2042696", 1, 200),
(3, "Maria Fernanda", "Marquez Silva", "MASM050224MNLTEFS6", "2043887", 2, 300);
select*from empleados;

create table usuarios(
	correo varchar(100) not null,
    password_ varchar(50) not null,
    id_empleado int not null,
	constraint FK_usuarios foreign key(id_empleado) references empleados(id_empleado)
);
-- USUARIOS
insert into usuarios (correo, password_, id_empleado) values
("teresa.zavalacnt@gob.mx", "Kdrama125", 1),
("david.garciab@gob.mx", "Kdrama123", 2),
("maria.marquezs@gob.mx", "Kdrama789", 3);
select*from usuarios;

CREATE TABLE enfermedades (
    id INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_nombre (nombre)
);
-- ENFERMEDADES 
INSERT INTO enfermedades (nombre) VALUES ('Fiebre amarilla'), ('Tuberculosis'), ('Cólera'), ('Dengue'), ('Difteria'), ('Tétanos'), ('Tosferina'), 
('Hepatitis A'), ('Hepatitis B'), ('Meningitis'), ('Bacteriemia'), ('Neumonía'), ('Epiglotitis'), ('Poliomielitis'), ('Influenza viral'), 
('Cáncer Cérvico-Uterino'), ('Rabia'), ('Diarrea por Rotavirus'), ('Sarampión'), ('Rubéola'), ('Parotiditis'), ('Varicela'), ('Fiebre tifoidea'), 
('Síndrome Agudo Respiratorio causado por el SARS-CoV2'), ('COVID-19'), ('Influenza'), ('Paperas'), ('Tos ferina'), ('VPH'), ('Chikungunya'), 
('Zika'), ('Malaria'), ('Polio'), ('Viruela'), ('Ébola');
SELECT*FROM ENFERMEDADES;
CREATE TABLE paises (
    id INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_nombre (nombre)
);
-- PAISES 
INSERT INTO paises (nombre) VALUES ('Seleccione un país'), ('Afganistán'), ('Albania'),('Alemania'),('Andorra'),('Angola'),('Antigua y Barbuda'),('Arabia Saudita'),('Argelia'),
('Argentina'),('Armenia'),('Australia'),('Austria'),('Azerbaiyán'),('Bahamas'),('Bangladés'),('Barbados'),('Baréin'),('Bélgica'),('Belice'),('Benín'),('Bielorrusia'),
('Birmania (Myanmar)'),('Bolivia'),('Bosnia y Herzegovina'),('Botsuana'),('Brasil'),('Brunéi'),('Bulgaria'),('Burkina Faso'),('Burundi'),('Bután'),
('Cabo Verde'),('Camboya'),('Camerún'),('Canadá'),('Catar'),('Chad'),('Chile'),('China'),('Chipre'),('Ciudad del Vaticano'),('Colombia'),('Comoras'),('Corea del Norte'),('Corea del Sur'),
('Costa de Marfil'),('Costa Rica'),('Croacia'),('Cuba'),('Dinamarca'),('Dominica'),('Ecuador'),('Egipto'),('El Salvador'),('Emiratos Árabes Unidos'),('Eritrea'),('Eslovaquia'),
('Eslovenia'),('España'),('Estados Unidos'),('Estonia'),('Etiopía'),('Filipinas'),('Finlandia'),('Fiyi'),('Francia'),('Gabón'),('Gambia'),('Georgia'),
('Ghana'),('Granada'),('Grecia'),('Guatemala'),('Guinea'),('Guinea-Bisáu'),('Guinea Ecuatorial'),('Guyana'),('Haití'),('Honduras'),('Hungría'),('India'),
('Indonesia'),('Irak'),('Irán'),('Irlanda'),('Islandia'),('Islas Marshall'),('Islas Salomón'),('Israel'),('Italia'),('Jamaica'),('Japón'),('Jordania'),('Kazajistán'),
('Kenia'),('Kirguistán'),('Kiribati'),('Kuwait'),('Laos'),('Lesoto'),('Letonia'),('Líbano'),('Liberia'),('Libia'),('Liechtenstein'),('Lituania'),('Luxemburgo'),('Macedonia del Norte'),
('Madagascar'),('Malasia'),('Malaui'),('Maldivas'),('Malí'),('Malta'),('Marruecos'),('Mauricio'),('Mauritania'),('México'),('Micronesia'),('Moldavia'),
('Mónaco'),('Mongolia'),('Montenegro'),('Mozambique'),('Namibia'),('Nauru'),('Nepal'),('Nicaragua'),('Níger'),('Nigeria'),('Noruega'),('Nueva Zelanda'),
('Omán'),('Países Bajos'),('Pakistán'),('Palaos'),('Palestina'),('Panamá'),('Papúa Nueva Guinea'),('Paraguay'),('Perú'),('Polonia'),('Portugal'),('Reino Unido'),
('República Centroafricana'),('República Checa'),('República del Congo'),('República Democrática del Congo'),('República Dominicana'),('Ruanda'),('Rumania'),
('Rusia'),('Samoa'),('San Cristóbal y Nieves'),('San Marino'),('San Vicente y las Granadinas'),('Santa Lucía'),('Santo Tomé y Príncipe'),('Senegal'),
('Serbia'),('Seychelles'),('Sierra Leona'),('Singapur'),('Siria'),('Somalia'),('Sri Lanka'),('Suazilandia (Eswatini)'),('Sudáfrica'),('Sudán'),('Sudán del Sur'),
('Suecia'),('Suiza'),('Surinam'),('Tailandia'),('Tanzania'),('Tayikistán'),('Timor Oriental'),('Togo'),('Tonga'),('Trinidad y Tobago'),('Túnez'),('Turkmenistán'),
('Turquía'),('Tuvalu'),('Ucrania'),('Uganda'),('Uruguay'),('Uzbekistán'),('Vanuatu'),('Venezuela'),('Vietnam'),('Yemen'),('Yibuti'),('Zambia'),('Zimbabue');
SELECT*FROM paises;

CREATE TABLE lotes(
    id_lote INT NOT NULL AUTO_INCREMENT,
    lote_vacunacion VARCHAR(50) NOT NULL,
    folio varchar(50) not null,
    id_pais int not null,
    empresa varchar(100) not null,
    nombre_comercial varchar(100) not null,
    fecha_caducidad DATE NOT NULL,
    cantidad INT NOT NULL,
    enfermedades VARCHAR(200),
    fecha timestamp default current_timestamp,
    activo BOOLEAN DEFAULT true,
    CONSTRAINT PK_lote PRIMARY KEY(id_lote),
    constraint UK_paises foreign key (id_pais) references paises(id),
    constraint UK_enfermedades foreign key(enfermedades) references enfermedades(nombre)
);
select*from lotes;
INSERT INTO lotes (lote_vacunacion, folio, id_pais, empresa, nombre_comercial, fecha_caducidad, cantidad, enfermedades) VALUES
-- INFLUENZA (AZTECA VACUNAS - MEXICO)
('FAZ00068',  '253300515A0698', (SELECT id FROM paises WHERE nombre = 'México'), 'AZTECA VACUNAS, S.A. DE C.V.',                                  'MEXINVAC',                                       '2026-09-30', 763400, 'Influenza viral'),

-- SPR - SARAMPIÓN, PAROTIDITIS Y RUBÉOLA (LABORATORIOS BIOLÓGICOS - INDIA)
('0135W090',  '253300515A0837', (SELECT id FROM paises WHERE nombre = 'India'),  'LABORATORIOS DE BIOLÓGICOS Y REACTIVOS DE MÉXICO, S.A. DE C.V.', 'VACUNA CONTRA SARAMPIÓN, PAROTIDITIS Y RUBÉOLA', '2027-03-31', 88300,  'Sarampión'),

-- VPH (MERCK SHARP - EUA)
('Z008285',   '253300515A0908', (SELECT id FROM paises WHERE nombre = 'Estados Unidos'), 'MERCK SHARP & DOHME COMERCIALIZADORA, S. DE R.L. DE C.V.',    'GARDASIL 9',                                     '2027-06-30', 1390,   'VPH'),

-- NEUMOCOCO (PFIZER - IRLANDA)
('MT2471',    '253300515A0632', (SELECT id FROM paises WHERE nombre = 'Irlanda'),        'PFIZER, S.A. DE C.V.',                                         'PREVENAR 20',                                    '2027-01-31', 246020, 'Neumonía'),

-- INFLUENZA ADICIONALES (AZTECA VACUNAS - MEXICO)
('FAZ00106',  '253300515A0831', (SELECT id FROM paises WHERE nombre = 'México'), 'AZTECA VACUNAS, S.A. DE C.V.',                                  'MEXINVAC',                                       '2026-09-30', 759400, 'Influenza viral');

CREATE TABLE asignacion_vacunas_centro(
    id_inventario INT NOT NULL AUTO_INCREMENT,
    id_centro INT NOT NULL,
    id_lote int not null, folio varchar(50) not null,
    cantidad_asignar int not null,
    fecha_asignacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    activo BOOLEAN DEFAULT TRUE,
    observaciones VARCHAR(200),
    CONSTRAINT PK_inventario_centro PRIMARY KEY(id_inventario),
    CONSTRAINT FK_inv_centro FOREIGN KEY(id_centro) REFERENCES centros_salud(id_centro),
    CONSTRAINT FK_inv_lote FOREIGN KEY(id_lote) REFERENCES lotes(id_lote)
); 
select*from asignacion_vacunas_centro;

CREATE TABLE campana_vacunacion(
    id_campana INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    descripcion VARCHAR(500),                   
    fecha_inicio DATE NOT NULL,                 
    fecha_fin DATE NOT NULL,                                 
    id_centro INT NOT NULL,                     
    activo BOOLEAN DEFAULT TRUE,
    CONSTRAINT PK_campana PRIMARY KEY(id_campana),
    CONSTRAINT FK_campana_centro FOREIGN KEY(id_centro) REFERENCES centros_salud(id_centro)
);
select*from campana_vacunacion;
INSERT INTO campana_vacunacion (nombre, tipo, descripcion, fecha_inicio, fecha_fin, id_centro, activo) VALUES
('Vacunación Antinfluenza', 'Estacional', 'Campaña de invierno contra influenza estacional para grupos de riesgo', '2025-10-01', '2025-12-15', 100, TRUE),
('Refuerzo COVID-19', 'COVID-19', 'Dosis de refuerzo para mayores de 60 años y personal de salud', '2025-11-01', '2026-01-31', 100, TRUE);

select*from campana_detalle_lote;
CREATE TABLE campana_detalle_lote(
    id_detalle INT NOT NULL AUTO_INCREMENT,
    id_campana INT NOT NULL,
    id_inventario INT NOT NULL,  
    cantidad_planificada INT NOT NULL,
    CONSTRAINT PK_detalle PRIMARY KEY(id_detalle),
    CONSTRAINT FK_detalle_campana FOREIGN KEY(id_campana) REFERENCES campana_vacunacion(id_campana),
    CONSTRAINT FK_detalle_inventario FOREIGN KEY(id_inventario) REFERENCES asignacion_vacunas_centro(id_inventario)
);

CREATE TABLE aplicador_vacunas(
    id_aplicacion INT AUTO_INCREMENT,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_campana INT NOT NULL,
    curp_paciente VARCHAR(18) NOT NULL,
    id_empleado INT NOT NULL,
    id_centro INT NOT NULL,
    lote varchar(50),
    CONSTRAINT PK_aplicador PRIMARY KEY(id_aplicacion),
    CONSTRAINT FK_av_campana FOREIGN KEY(id_campana) REFERENCES campana_vacunacion(id_campana),
    CONSTRAINT FK_av_paciente FOREIGN KEY(curp_paciente) REFERENCES ciudadanos(curp),
    CONSTRAINT FK_av_empleado FOREIGN KEY(id_empleado) REFERENCES empleados(id_empleado),
    CONSTRAINT FK_av_centro FOREIGN KEY(id_centro) REFERENCES centros_salud(id_centro)
);
select*from aplicador_vacunas; 


CREATE TABLE notificaciones (
    id_notificacion    INT AUTO_INCREMENT PRIMARY KEY,
    titulo             VARCHAR(100) NOT NULL,
    mensaje            TEXT NOT NULL,
    fecha              DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tipo               ENUM('Brote', 'Alerta', 'Informacion') NOT NULL,
    fecha_publicacion  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estados 		   VARCHAR(100) NOT NULL
);

CREATE TABLE notificaciones_leidas (
    id_ciudadano     INT NOT NULL,
    id_notificacion  INT NOT NULL,
    fecha_lectura    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT PK_nl          PRIMARY KEY (id_ciudadano, id_notificacion),
    CONSTRAINT FK_nl_ciudadano FOREIGN KEY (id_ciudadano)    REFERENCES ciudadanos(id_ciudadano),
    CONSTRAINT FK_nl_notif     FOREIGN KEY (id_notificacion) REFERENCES notificaciones(id_notificacion)
);


-- ============================================================
-- 1. LOTES NUEVOS (Hepatitis A, Hepatitis B y Embarazadas)
-- ============================================================
INSERT INTO lotes (lote_vacunacion, folio, id_pais, empresa, nombre_comercial, fecha_caducidad, cantidad, enfermedades) VALUES
('HAV-2025-001', '253300516A1101', (SELECT id FROM paises WHERE nombre = 'Bélgica'),
 'GSK, S.A. DE C.V.', 'HAVRIX', '2027-06-30', 95000, 'Hepatitis A'),

('HBV-2025-001', '253300516A1102', (SELECT id FROM paises WHERE nombre = 'Bélgica'),
 'GSK, S.A. DE C.V.', 'ENGERIX-B', '2027-08-31', 85000, 'Hepatitis B'),

('INF-EMB-2025-001', '253300516A1103', (SELECT id FROM paises WHERE nombre = 'México'),
 'AZTECA VACUNAS, S.A. DE C.V.', 'MEXINVAC', '2026-09-30', 60000, 'Influenza viral');

SELECT * FROM lotes;
INSERT INTO lotes (lote_vacunacion, folio, id_pais, empresa, nombre_comercial, fecha_caducidad, cantidad, enfermedades) VALUES
('EW0182', '253300515A1045', 
 (SELECT id FROM paises WHERE nombre = 'Irlanda'), 
 'PFIZER, S.A. DE C.V.', 'COMIRNATY', '2026-12-31', 180000, 'COVID-19');


-- ============================================================
-- 2. ASIGNACIÓN AL CENTRO 100 (Nuevo León)
-- ============================================================
INSERT INTO asignacion_vacunas_centro (id_centro, id_lote, folio, cantidad_asignar, observaciones) VALUES
(100, (SELECT id_lote FROM lotes WHERE lote_vacunacion = 'HAV-2025-001'),     '253300516A1101', 30000, 'Asignación Hepatitis A adultos - Hospital Metropolitano NL'),
(100, (SELECT id_lote FROM lotes WHERE lote_vacunacion = 'HBV-2025-001'),     '253300516A1102', 30000, 'Asignación Hepatitis B adultos - Hospital Metropolitano NL'),
(100, (SELECT id_lote FROM lotes WHERE lote_vacunacion = 'INF-EMB-2025-001'),'253300516A1103', 20000, 'Asignación Influenza embarazadas - Hospital Metropolitano NL'),
(100, (SELECT id_lote FROM lotes WHERE lote_vacunacion = 'EW0182'),           '253300515A1045', 40000, 'Asignación COVID-19 adultos mayores - Hospital Metropolitano NL');

SELECT * FROM asignacion_vacunas_centro;

-- ============================================================
-- 3. CAMPAÑAS (solo mayores de edad, Centro 100 NL)
-- ============================================================
INSERT INTO campana_vacunacion (nombre, tipo, descripcion, fecha_inicio, fecha_fin, id_centro, activo) VALUES

('Campaña Hepatitis A Adultos NL 2025',
 'Preventiva',
 'Vacunación gratuita contra Hepatitis A dirigida a adultos de 18 años en adelante, con énfasis en trabajadores de salud, manipuladores de alimentos y viajeros internacionales en Nuevo León.',
 '2025-08-01', '2025-11-30', 100, TRUE),

('Campaña Hepatitis B Adultos NL 2025',
 'Preventiva',
 'Vacunación gratuita contra Hepatitis B para adultos de 18 años en adelante, con prioridad en personal de salud, pacientes con enfermedades crónicas y población en riesgo en Nuevo León.',
 '2025-08-01', '2025-11-30', 100, TRUE),

('Campaña Refuerzo COVID-19 Adultos NL 2025',
 'COVID-19',
 'Dosis de refuerzo de vacuna COVID-19 para adultos mayores de 18 años, con prioridad en personas de 50 años en adelante, personal de salud y pacientes con comorbilidades en Nuevo León.',
 '2025-11-01', '2026-02-28', 100, TRUE),

('Campaña Influenza Mujeres Embarazadas NL 2025',
 'Estacional',
 'Vacunación prioritaria contra influenza estacional para mujeres embarazadas en cualquier trimestre de gestación. Protege a la madre y al bebé durante los primeros meses de vida.',
 '2025-10-01', '2025-12-31', 100, TRUE);

SELECT * FROM campana_vacunacion;

-- ============================================================
-- 4. DETALLE DE LOTES POR CAMPAÑA
-- ============================================================
INSERT INTO campana_detalle_lote (id_campana, id_inventario, cantidad_planificada) VALUES

-- Hepatitis A → lote HAV-2025-001
((SELECT id_campana FROM campana_vacunacion WHERE nombre = 'Campaña Hepatitis A Adultos NL 2025'),
 (SELECT id_inventario FROM asignacion_vacunas_centro WHERE id_centro = 100 AND id_lote = (SELECT id_lote FROM lotes WHERE lote_vacunacion = 'HAV-2025-001')),
 25000),

-- Hepatitis B → lote HBV-2025-001
((SELECT id_campana FROM campana_vacunacion WHERE nombre = 'Campaña Hepatitis B Adultos NL 2025'),
 (SELECT id_inventario FROM asignacion_vacunas_centro WHERE id_centro = 100 AND id_lote = (SELECT id_lote FROM lotes WHERE lote_vacunacion = 'HBV-2025-001')),
 25000),

-- COVID-19 → lote EW0182
((SELECT id_campana FROM campana_vacunacion WHERE nombre = 'Campaña Refuerzo COVID-19 Adultos NL 2025'),
 (SELECT id_inventario FROM asignacion_vacunas_centro WHERE id_centro = 100 AND id_lote = (SELECT id_lote FROM lotes WHERE lote_vacunacion = 'EW0182')),
 30000),

-- Influenza embarazadas → lote INF-EMB-2025-001
((SELECT id_campana FROM campana_vacunacion WHERE nombre = 'Campaña Influenza Mujeres Embarazadas NL 2025'),
 (SELECT id_inventario FROM asignacion_vacunas_centro WHERE id_centro = 100 AND id_lote = (SELECT id_lote FROM lotes WHERE lote_vacunacion = 'INF-EMB-2025-001')),
 18000);

SELECT * FROM campana_detalle_lote;

-- ============================================================
-- 5. APLICADOR DE VACUNAS
--    David (médico, id 2) aplica a ciudadanos adultos de NL
-- ============================================================
INSERT INTO aplicador_vacunas (id_campana, curp_paciente, id_empleado, id_centro, lote) VALUES

-- Teresa (ciudadana, 20 años) recibe Hepatitis A
((SELECT id_campana FROM campana_vacunacion WHERE nombre = 'Campaña Hepatitis A Adultos NL 2025'),
 'ZACT041217MNLVNRA2', 2, 100, 'HAV-2025-001'),

-- Luis Fernando (35 años) recibe Hepatitis B
((SELECT id_campana FROM campana_vacunacion WHERE nombre = 'Campaña Hepatitis B Adultos NL 2025'),
 'ZACT041217MNLVNRA2', 2, 100, 'HBV-2025-001'),


((SELECT id_campana FROM campana_vacunacion WHERE nombre = 'Campaña Refuerzo COVID-19 Adultos NL 2025'),
 'ZACT041217MNLVNRA2', 2, 100, 'EW0182'),

((SELECT id_campana FROM campana_vacunacion WHERE nombre = 'Campaña Influenza Mujeres Embarazadas NL 2025'),
 'ZACT041217MNLVNRA2', 2, 100, 'INF-EMB-2025-001'),


((SELECT id_campana FROM campana_vacunacion WHERE nombre = 'Campaña Hepatitis B Adultos NL 2025'),
 'ZACT041217MNLVNRA2', 2, 100, 'HBV-2025-001');

SELECT * FROM aplicador_vacunas;

-- ============================================================
-- 6. ESAVI — Reacción adversa reportada por Sofía tras vacuna embarazadas
--    David como médico responsable (cédula 2042696)
-- ============================================================
INSERT INTO esavi (curp_cuidadano, nombre_cuidadano, cedula_doctor, id_lote, id_centro, descripcion) VALUES
('REGS981104MNLYNA8', 'Sofía Isabel Reyes Guzmán', '2042696',
 (SELECT id_lote FROM lotes WHERE lote_vacunacion = 'INF-EMB-2025-001'),
 100,
 'Paciente embarazada presenta dolor moderado en zona de aplicación, fiebre leve de 37.6°C y malestar general durante 24 horas posteriores a la aplicación de vacuna contra Influenza.');

SELECT * FROM esavi;

-- ============================================================
-- 7. NOTIFICACIONES — Enfocadas en adultos, Nuevo León
-- ============================================================
INSERT INTO notificaciones (titulo, mensaje, tipo, estados) VALUES


('Protege tu Embarazo: Vacúnate contra la Influenza en NL',
 'Todas las mujeres embarazadas en cualquier trimestre pueden recibir gratuitamente la vacuna contra influenza en el Hospital Metropolitano de San Nicolás. La vacuna protege a la madre y al recién nacido.',
 'Informacion', 'Nuevo León'),

('Alerta: Casos de Hepatitis A en Zona Metropolitana de Monterrey',
 'Se registran casos de Hepatitis A en municipios de la ZMM. Se exhorta a adultos no vacunados a acudir a su centro de salud. Refuerza medidas de higiene y evita alimentos de dudosa procedencia.',
 'Alerta', 'Nuevo León'),

('Brote COVID-19 Activo en Municipios de Nuevo León',
 'Autoridades de salud confirman incremento de casos COVID-19 en Monterrey, San Nicolás y Guadalupe. Se activa jornada de refuerzo de vacunación para adultos en el Hospital Metropolitano Bernardo Sepúlveda.',
 'Brote', 'Nuevo León');
