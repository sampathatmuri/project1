/*function loadjson(file,callback) {
    var xhr = new XMLHttpRequest();
    xhr.overrideMimeType("application/json");
    xhr.open("Get",file,true);
    xhr.onreadystatechange = function() {
      if(xhr.readyState === 4 && xhr.status == "200"){
        callback(xhr.responseText);
      }
    };
    xhr.send(null);
  }
loadjson("data.json",function(text)
{
var data= JSON.parse(text);
console.log(data);
basics(data.details);
carrerinfo(data.carrer);

education(data.education);
technical(data.technicalskills);
achievements(data.achievements);
})
*/


function loadjson(file){
  return new Promise((resolve,reject)=>{
    return fetch(file).then(response=>{
      if (response.ok) {
        resolve(response.json());

      }
      else {
        reject(new Error('error'));
      }
    })
  })
}

var newfile =loadjson('data.json');
newfile.then(data=>{
  console.log(data);
  basics(data.details);
  carrerinfo(data.carrer);

  education(data.education);
  technical(data.technicalskills);
  achievements(data.achievements);
})

var childone = document.querySelector(".child1");
function basics(det) {

  var image = document.createElement("img");
  image.src = det.image;
  childone.appendChild(image);

  var name = document.createElement("h4");
  name.textContent = det.name;
  childone.appendChild(name);

  var phone = document.createElement("h4");
  phone.textContent = det.phone;
  childone.appendChild(phone);

  var email = document.createElement("a");
  email.href  = "mailto:urwithdreamracer@gmail.com";
  email.textContent = det.email;
  childone.appendChild(email);

  var add = document.createElement("h2");
  add.textContent = "Address";
  childone.appendChild(add);

  var hr = document.createElement("hr");
  childone.appendChild(hr);

  var p = document.createElement("p");
  p.textContent = det.Address;
  childone.appendChild(p);



}
var childtwo = document.querySelector(".child2");
function carrerinfo(info) {
  var heading = document.createElement("h2");
  heading.textContent="carrer objective:";
  childtwo.append(heading);

  var hr = document.createElement("hr");
  childtwo.appendChild(hr);

  var c = document.createElement("h4");
  c.textContent = info.info;
  childtwo.appendChild(c);

  var hr = document.createElement("hr");
  childone.appendChild(hr);


}
function education(edu){
  var hd=document.createElement("h2");
  hd.textContent="Educational Qualifications";
  childtwo.appendChild(hd);

  var hr = document.createElement("hr");
  childtwo.appendChild(hr);

  var table1 = document.createElement("table");
  table1.border = "1";
  childtwo.appendChild(table1);

  tabledata = "";
  for(i=0;i<edu.length;i++){

    tabledata+= "<tr><td>"+edu[i].qualification+"</td><td>"+edu[i].institute+"</td><td>"+edu[i].year+"</td><td>"+edu[i].percentage+"</td></tr>";

  }
  table1.innerHTML = tabledata;

}

function technical(tec) {

var hd = document.createElement("h2");
hd.textContent = "Technical Skills";
childtwo.appendChild(hd);

var hr = document.createElement("hr");
childtwo.appendChild(hr);

for(i=0;i<tec.length;i++){
  var title = document.createElement("h4");
  title.textContent = tec[i].title;
  childtwo.appendChild(title);

  var edul = document.createElement("ul");
  var eduli = document.createElement("li");
  eduli.textContent = tec[i].info;
  edul.appendChild(eduli);
  childtwo.appendChild(edul);
}

}
function achievements(act){

  var hd = document.createElement("h2");
  hd.textContent = "Achievements";
  childtwo.appendChild(hd);

  var hr = document.createElement("hr");
  childtwo.appendChild(hr);

  for(i=0;i<act.length;i++){
    var title = document.createElement("h4");
    title.textContent = act[i].title;
    childtwo.appendChild(title);

    var edul = document.createElement("ul");
    var eduli = document.createElement("li");
    eduli.textContent = act[i].info;
    edul.appendChild(eduli);
    childtwo.appendChild(edul);
  }

}
function openpage()
{
  window.open("1.html","_self",true);
}
