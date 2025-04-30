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

select * from hboard;



-- boardupfiles 테이블
CREATE TABLE `jis`.`boardupfiles` (
  `fileNo` INT NOT NULL AUTO_INCREMENT,
  `originalFileName` VARCHAR(100) NOT NULL,
  `newFileName` VARCHAR(150) NOT NULL,
  `thumbFileName` VARCHAR(150) NULL,
  `isImage` TINYINT(4) NULL DEFAULT 0,
  `ext` VARCHAR(20) NULL,
  `size` BIGINT(20) NULL,
  `boardNo` INT NULL,
  `base64` LONGTEXT NULL,
  `filePath` VARCHAR(200) NULL,
  PRIMARY KEY (`fileNo`),
  INDEX `fk_upfiles_hboard_idx` (`boardNo` ASC) VISIBLE,
  CONSTRAINT `fk_upfiles_hboard`
    FOREIGN KEY (`boardNo`)
    REFERENCES `jis`.`hboard` (`boardNo`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
COMMENT = '게시물에 업로드되는 파일정보';

-- 업로드 파일 저장
insert into boardupfiles (
	boardNo, originalFileName, newFileName, thumbFileName, isImage, ext, size, base64, filePath
) values (
	#{boardNo}, #{originalFileName}, #{newFileName}, #{thumbFileName}, #{isImage}, #{ext}, #{size}, #{base64}, #{filePath}
);

select * from boardupfiles where boardNo = 7;

-- 8번 글 : hboard, boardupfiles, member
-- 조인
select h.boardNo, h.title, h.content, h.writer, h.postDate, h.readCount, h.ref, h.step, h.refOrder,
f.*, m.memberName, m.email
from hboard h left outer join boardupfiles f
on h.boardNo = f.boardNo
inner join member m
on h.writer = m.memberId
where h.boardNo = 8;