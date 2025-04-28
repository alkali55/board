-- member 테이블
CREATE TABLE `jis`.`member` (
  `memberId` VARCHAR(8) NOT NULL,
  `memberPwd` VARCHAR(200) NOT NULL,
  `memberName` VARCHAR(12) NULL,
  `mobile` VARCHAR(13) NULL,
  `email` VARCHAR(50) NULL,
  `registerDate` DATETIME NULL DEFAULT now(),
  `memberImg` VARCHAR(45) NOT NULL DEFAULT 'avatar.png',
  `memberPoint` INT NULL DEFAULT 100,
  `gender` VARCHAR(1) NOT NULL DEFAULT 'U',
  PRIMARY KEY (`memberId`),
  UNIQUE INDEX `mobile_UNIQUE` (`mobile` ASC) VISIBLE,
  UNIQUE INDEX `emial_UNIQUE` (`email` ASC) VISIBLE);

-- 회원 저장
use jis;
insert into member(memberId, memberPwd, memberName, mobile, email) 
		values('asdf123', sha1(md5('1234')), '홍길동', '010-1111-2222', 'abc@abc.com');

-- 계층형 게시판 테이블
CREATE TABLE `jis`.`hboard` (
  `boardNo` INT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(20) NOT NULL,
  `content` VARCHAR(2000) NULL,
  `writer` VARCHAR(8) NULL,
  `postDate` DATETIME NULL DEFAULT now(),
  `readCount` INT NULL DEFAULT 0,
  `ref` INT NULL DEFAULT 0,
  `step` INT NULL DEFAULT 0,
  `refOrder` INT NULL DEFAULT 0,
  PRIMARY KEY (`boardNo`),
  INDEX `fk_hboard_member_idx` (`writer` ASC) VISIBLE,
  CONSTRAINT `fk_hboard_member`
    FOREIGN KEY (`writer`)
    REFERENCES `jis`.`member` (`memberId`)
    ON DELETE SET NULL
    ON UPDATE NO ACTION)
COMMENT = '계층형 게시판';

insert into hboard(title, content, writer) values('게시판 생성', '많은 이용 바랍니다', 'asdf123');