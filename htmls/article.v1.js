const api = 'https://ch30tnydq2.execute-api.ap-northeast-2.amazonaws.com/default';

var num = -1;
var ver = -1;
var kr = true;

function set(_n, _v, _k) {
  num = _n;
  ver = _v;
  kr = _k;

  let m0 = kr ? '다른 글 읽기' : 'Read other articles';
  let m1 = kr ? '전체 글 목록' : 'A list of all articles';
  let m2 = kr ? '댓글' : 'Comments';
  let m3 = kr ? '이름' : 'Name';
  let m4 = kr ? '내용' : 'Content';
  let m5 = kr ? '댓글 달기' : 'Save';

  $('body').append(`<hr>`);

  $('body').append(`<h2>${m0}</h2>`);
  $('body').append(`<a href="./main">${m1}</a>`);
  $('body').append(`<div id="others"></div>`);

  $('body').append(`<h2>${m2}</h2>`);
  $('body').append(`<input type="text" id="comment-name" placeholder="${m3}"></input>`);
  $('body').append(`<textarea id="comment-content" rows="5" placeholder="${m4}"></textarea>`);
  $('body').append(`<button id="comment-save" onclick="comment()">${m5}</button>`);
  $('body').append(`<div id="comments"></div>`);
}

function latest() {
  axios.get(`${api}/article?num=${num}`)
  .then(res => {
    const latest = parseInt(res.data.kr_latest.N);
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
      const time = kr ? `${year}년 ${month}월 ${date}일 ${hour}시 ${min}분` : `${date} ${monthNames[month]} ${year} ${hour}:${min}`;
      c.content.split('\n').forEach(l => {
        $('#comments').append(`<p class="comment-content">${l}</p>`);
      });
      $('#comments').append(`<p class="comment-time">${time}</p>`);
    })
  });
}

function comment() {
  const name = encodeURIComponent($('#comment-name').val());
  const content = encodeURIComponent($('#comment-content').val());
  if (name && content)
    $('#comment-save').attr('disabled', '');
    axios.post(`${api}/comment`, `name=${name}&content=${content}&article=${num}`)
    .then(res => {
      if (res.data.success) {
        comments();
        $('#comment-name').val('');
        $('#comment-content').val('');
      }
    })
    .finally(() => {
      $('#comment-save').removeAttr('disabled');
    });
}
