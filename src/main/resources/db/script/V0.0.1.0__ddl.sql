ALTER TABLE `ContractOrder`
ADD COLUMN `annexFile` varchar(255) NULL COMMENT '附件文件' AFTER `workerId`;