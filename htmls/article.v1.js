const api = 'https://ch30tnydq2.execute-api.ap-northeast-2.amazonaws.com/default';

var num = -1;
var ver = -1;
var kr = true;
var ps = [];
var ws = [];
var words = [];
var wm = {};
var last = undefined;

function set(_n, _v, _k) {
  num = _n;
  ver = _v;
  kr = _k;
  let p = $('p');
  for (let i = 0; i < p.length; i++) ps.push(p[i]);

  let m0 = kr ? '다른 글 읽기' : 'Read other articles';
  let m1 = kr ? '전체 글 목록' : 'Go to the page of all articles';
  let m2 = kr ? '댓글' : 'Comments';
  let m3 = kr ? '이름' : 'Name';
  let m3_1 = kr ? '이메일(필수 아님)' : 'Email (optional)';
  let m4 = kr ? '내용' : 'Content';
  let m5 = kr ? '댓글 달기' : 'Save';

  $('body').append(`<hr>`);

  $('body').append(`<h2>${m0}</h2>`);
  $('body').append(`<a href="./main">${m1}</a>`);
  $('body').append(`<div id="others"></div>`);

  $('body').append(`<h2>${m2}</h2>`);
  $('body').append(`<input type="text" id="comment-name" placeholder="${m3}"></input>`);
  $('body').append(`<input type="text" id="comment-email" placeholder="${m3_1}"></input>`);
  $('body').append(`<textarea id="comment-content" rows="5" placeholder="${m4}"></textarea>`);
  $('body').append(`<button id="comment-save" onclick="comment()">${m5}</button>`);
  $('body').append(`<div id="comments"></div>`);
}

function latest() {
  axios.get(`${api}/article?num=${num}`)
  .then(res => {
    const latest = parseInt(kr ? res.data.kr_latest.N : res.data.en_latest.N);
    const msg0 = kr ? '글이 최신 판이 아닙니다.' : 'Not the latest version.';
    const msg1 = kr ? '최신 판 보러가기' : 'Go to the latest';
    const lang = kr ? 'kr' : 'en';
    if (latest > ver)
      $('body').append(`
      <div id="version-modal" class="w3-modal">
        <div class="w3-modal-content">
          <div class="w3-container">
            <span onclick="document.getElementById('version-modal').style.display='none'" class="w3-button w3-display-topright">&times;</span>
            <p style="color:red;">${msg0}&nbsp;<a href="./${lang}_${num}_${latest}">${msg1}</a></p>
           </div>
        </div>
      </div>`);
  });
}

function others() {
  axios.get(`${api}/articles-near?num=${num}&delta=2`)
  .then(res => {
    const lis = res.data.sort((a, b) => parseInt(a.article_num.N) - parseInt(b.article_num.N)).map(a => {
      const anum = parseInt(a.article_num.N);
      const krlt = a.kr_latest;
      const enlt = a.en_latest;
      const krlk = krlt ? `./kr_${anum}_${krlt.N}` : undefined;
      const enlk = enlt ? `./en_${anum}_${enlt.N}` : undefined;
      const kra = (kr && num === anum && krlt) ? `<strong>${a.kr_title.S}</strong>` : (krlk ? `<a href="${krlk}">${a.kr_title.S}</a>` : undefined);
      const ena = (!kr && num === anum && enlt) ? `<strong>${a.en_title.S}</strong>` : (enlk ? `<a href="${enlk}">${a.en_title.S}</a>` : undefined);
      const str = (kra && ena) ? `${kra} | ${ena}` : (kra ? kra : ena);
      return `<li>${str}</li>`;
    }).join('\n');
    $('#others').append(`<ul>${lis}</ul>`);
  });
}

const monthNames = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
function comments() {
  axios.get(`${api}/comments?article=${num}`)
  .then(res => {
    $('#comments').html('');
    res.data.forEach(c => {
      $('#comments').append(`<p class="comment-name">${c.name.substring(0, 30)}</p>`);
      function to2(a) { return (!kr && a < 10) ? ('0' + a) : a; }
      const d = new Date(c.ts);
      const year = d.getYear() + 1900;
      const month = d.getMonth() + 1;
      const date = d.getDate();
      const hour = to2(d.getHours());
      const min = to2(d.getMinutes());
      const time = kr ? `${year}년 ${month}월 ${date}일 ${hour}시 ${min}분` : `${date} ${monthNames[month-1]} ${year} ${hour}:${min}`;
      c.content.split('\n').forEach(l => {
        $('#comments').append(`<p class="comment-content">${l}</p>`);
      });
      $('#comments').append(`<p class="comment-time">${time}</p>`);
    })
  });
}

function comment() {
  const name = encodeURIComponent($('#comment-name').val());
  const email = encodeURIComponent($('#comment-email').val());
  const content = encodeURIComponent($('#comment-content').val());
  if (name && content) {
    $('#comment-save').attr('disabled', '');
    axios.post(`${api}/comment`, `name=${name}&content=${content}&article=${num}&email=${email}`)
    .then(res => {
      if (res.data.success) {
        comments();
        $('#comment-name').val('');
        $('#comment-email').val('');
        $('#comment-content').val('');
      }
    })
    .finally(() => {
      $('#comment-save').removeAttr('disabled');
    });
  }
}

function wordsInit() {
  if (kr) {
    axios.get(`${api}/words`)
    .then(res => {
      words = res.data.map(a => a.kor.S).filter((v, i, a) => a.indexOf(v) === i);
      ws = ps.map(p => words.filter(w => {
        let i = p.innerHTML.indexOf(w);
        return i == 0 || (i > 0 && p.innerHTML[i - 1] === ' ');
      }));
      words.forEach(w => {
        wm[w] = res.data.filter(a => a.kor.S === w).map(a => a.eng.S).join('; ');
      });
      $('body').append('<div id="dict-button-div"><div id="dict-button-div2"><button id="dict-button" onclick="showWords()">사전 열기</button></div></div>');
      $('body').append(`
      <div id="dict-modal" class="w3-modal" style="display: none;">
        <div class="w3-modal-content">
          <div class="w3-container">
            <span onclick="document.getElementById('dict-modal').style.display='none'" class="w3-button w3-display-topright">&times;</span>
            <a href="dictionary" target="_blank" rel="noopener noreferrer">전체 사전 보기</a>
            <table id="dict-table"><tbody id="dict-body"></tbody></table>
          </div>
        </div>
      </div>`);
      document.addEventListener('selectionchange', repWords)
    });
  }
}

function showWords() {
  if (kr) {
    let h = $(window).height();
    let cs = [];
    for (let i = 0; i < ps.length; i++) {
      let r = ps[i].getBoundingClientRect();
      let s = r.y;
      let e = s + r.height;
      if (0 <= e && s <= h) {
        ws[i].forEach(w => {
          if (!cs.includes(w)) cs.push(w);
        });
      }
    }
    $('#dict-body').text('');
    cs.sort().forEach(w => {
      $('#dict-body').append(`<tr><td>${w}</td><td>${wm[w]}</td></tr>`);
    });
    document.getElementById('dict-modal').style.display='block'
  }
}

function repWords() {
  let curr = new Date().getTime();
  last = curr;
  let s = document.getSelection();
  let a = s.anchorNode;

  if (a === s.focusNode && a !== null) {
    let ao = Math.min(s.anchorOffset, s.focusOffset);
    let fo = Math.max(s.anchorOffset, s.focusOffset);

    if (fo - ao < 20) {
      let text = a.data.substring(ao, fo);
      let fw = words.filter(w =>
        text.includes(w) &&
        (ao + text.indexOf(w) === 0 || a.data[ao + text.indexOf(w) - 1] === ' ') &&
        a.data[ao + text.indexOf(w) + w.length] !== '('
      ).sort((a, b) => b.length - a.length);

      if (fw.length > 0) {
        let kor = fw[0];
        let res = kor + '(' + wm[kor] + ')';
        let rep = text.replace(kor, res);
        let pre = a.data.substring(0, ao);
        let post = a.data.substring(fo);

        setTimeout(() => {
          if (last === curr) {
            s.empty();
            a.data = pre + rep + post;
          }
        }, 200);
      }
    }
  }
}
