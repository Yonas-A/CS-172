
const handleSearch = event => {
  function handleForm(event) {
    event.preventDefault();
  }
  let searchString = document.getElementById('query').value.trim().toLowerCase();
};

function start(e) {
    query = document.getElementById("query").value;
    id = document.getElementById("id").value;
   if(query || id) {
        console.log("test found")
        console.log(query);
        console.log(id);
   }
   else{
        console.log("not found found")
   }
}

async function testFetch() {
  const URL = "http://localhost:8080/api/v1/tweet";
  const response = await fetch(URL);
  const data = await response.json();
  displayTweets(data);
}

function displayTweets(data) {
  let displayOutput = '';

  data.forEach(function (item) {
    var simple = item.value0; // tweet object but name used was already in use
    var score = item.value1;
    tweet = {};
    Object.keys(simple).forEach(key => (tweet[key] = simple[key]));
    const date = new Date(tweet.createdAt)
    const newDate = myTime(date);
    displayOutput += `
      <div class="tweet-block align-bottom d-flex ml-4" >
        <div class="d-flex align-bottom">
          <div class="align-right d-flex flex-column">
            <p class="tweet--score_area">${score}</p>
            <div class="d-flex bg-primary rounded" >
              <p><b>${tweet.name}</b> &nbsp; <b>${newDate? newDate: tweet.createdAt}</b></p>
              <p class="tweet-info">${tweet.language}<br /></p>
            </div>
            <p class="tweet--text__area">${tweet.text}</p>
            <p class="tweet--text__area">${tweet.title}</p>

          </div>
        </div>
      </div>
    `;
    console.log(tweet, score);
  });

  document.getElementById('display-field').innerHTML = displayOutput;
}


function myTime(time){
  if(time)  {
    var month = time.getMonth() + 1;
    var day = time.getDate();
    month = month > 9 ? month : "0"+month;
    day = day > 9 ? day : "0"+day;
    var hours = time.getHours();
    var min = time.getMinutes();
    return (month+"-"+day+"-"+time.getFullYear() +"  " +hours+ ":" +min)
  }
  return ("");
}