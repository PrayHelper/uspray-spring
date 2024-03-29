--4번째 실행
ALTER TABLE share
    RENAME TO shared_pray;

ALTER TABLE shared_pray
    DROP CONSTRAINT fk_share_storage_id_storage;
ALTER TABLE shared_pray
    DROP CONSTRAINT pk_share;

-- 1. shared_pray_id 열 추가
ALTER TABLE shared_pray
    ADD COLUMN shared_pray_id BIGSERIAL UNIQUE;

-- 2. shared_pray_id 열이 NULL이 아닌 행을 선택하여 시퀀스 변경
-- DROP SEQUENCE shared_pray_shared_pray_id_seq;
-- ALTER COLUMN shared_pray_id SET DATA TYPE BIGSERIAL;
-- CREATE SEQUENCE shared_pray_shared_pray_id_seq AS integer;
-- SELECT SETVAL('shared_pray_shared_pray_id_seq', COALESCE(MAX(shared_pray_id), 0) + 1)
-- FROM shared_pray;

-- 3. 시퀀스를 사용하여 shared_pray_id 열 업데이트
-- UPDATE shared_pray
-- SET shared_pray_id = nextval('shared_pray_shared_pray_id_seq');

-- 4. shared_pray_id 열을 기본 키로 설정
ALTER TABLE shared_pray
    ADD PRIMARY KEY (shared_pray_id);

--EDIT COLUMN
DELETE
FROM shared_pray
WHERE deleted_at IS NOT NULL;
ALTER TABLE shared_pray
    DROP COLUMN deleted_at;
ALTER TABLE shared_pray
    ADD COLUMN updated_at TIMESTAMP;
UPDATE shared_pray
SET updated_at = created_at;
ALTER TABLE shared_pray
    RENAME COLUMN pray_id TO legacy_pray_id;
ALTER TABLE shared_pray
    RENAME COLUMN storage_id TO pray_id;
ALTER TABLE shared_pray
    ALTER COLUMN pray_id TYPE BIGINT;

ALTER TABLE shared_pray
    DROP COLUMN receipt_id;
ALTER TABLE shared_pray
    DROP COLUMN legacy_pray_id;

ALTER TABLE shared_pray
    ADD CONSTRAINT fk_shared_pray_pray_id_pray FOREIGN KEY (pray_id) REFERENCES pray (pray_id);

