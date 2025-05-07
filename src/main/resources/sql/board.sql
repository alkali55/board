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

-- 조회수 처리
-- boardreadlog 테이블 생성
CREATE TABLE `jis`.`boardreadlog` (
  `boardReadLogNo` INT NOT NULL AUTO_INCREMENT,
  `readWho` VARCHAR(130) NOT NULL,
  `readWhen` DATETIME NULL DEFAULT now(),
  `boardNo` INT NULL,
  PRIMARY KEY (`boardReadLogNo`),
  INDEX `fk_boardreadlog_boardno_idx` (`boardNo` ASC) VISIBLE,
  CONSTRAINT `fk_boardreadlog_boardno`
    FOREIGN KEY (`boardNo`)
    REFERENCES `jis`.`hboard` (`boardNo`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
COMMENT = '게시글 조회 정보를 저장하는 테이블';

-- ipAddr유저가 boardNo번 글을 조회한 적이 있는지?
select * from boardreadlog where boardNo = 8 and readWho = '127.0.0.1';

insert into boardreadlog (readWho, boardNo) values ('127.0.0.1', 8);

select DATEDIFF(now(), readWhen) as datediff from boardreadlog where readWho = '127.0.0.1' and boardNo = 8;

select ifnull((select DATEDIFF(now(), readWhen) from boardreadlog where readWho = '127.0.0.1' and boardNo = 8), -1);

-- hboard에서 조회수(readCount) 1 증가
update hboard set readcount = readcount + 1 where boardNo = 8;

-- 24시간이 지났다면, readWhen을 업데이트
update boardreadlog set readWhen = now() where readWho = '127.0.0.1' and boardNo = 8;

-- 답글 처리

-- 부모글에 대한 다른 답글이 있는 상태에서, 부모글의 답글이 추가되는 경우
-- (자리확보를 위해) 기존의 답글의 refOrder값을 update해야 한다.
update hboard set refOrder = refOrder + 1 where ref = #{ref} and refOrder > #{refOrder};

-- 답글을 입력받아서 답글을 저장하고, 부모글의 boardNo를 ref에, 
-- 부모글의 step + 1을 step에, 부모글의 refOrder + 1을 refOrder에 저장한다.
insert into hboard (title, content, writer, ref, step, refOrder)
values(#{title}, #{content}, #{writer}, #{ref}, #{step}, #{refOrder});