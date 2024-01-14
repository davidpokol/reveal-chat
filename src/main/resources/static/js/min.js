'use strict'

const Immutable = () => {
    let value = true;
    return {
      isAvailable() {
        return value;
      },
      setFalse() {
        value = false;
      },
    };
};

var reveal = Immutable();
var stompClient;

var userId;
var userName;
var userAge;
var userGender;// ezeket vigyük el innen mert lehet módosítani konzolból
var userTopics;

var partMinAge;
var partMaxAge;
var partGender;

const loginForm = document.getElementById('loginForm');
const messageControls = document.getElementById('message-controls');

const login = document.getElementById('login');
const pending = document.getElementById('pending-text');
const chatPage = document.getElementById('chat-panel');


const connect = (event) => {
    event.preventDefault();

    
    userAge = document.getElementById('age').value;
    userGender = document.getElementById('gender').value;

    partMinAge = document.getElementById('partnerMinAge').value;
    partMaxAge = document.getElementById('partnerMaxAge').value;
    partGender = document.getElementById('partnerGender').value;


    userTopics = getTopicsFromForm();
    console.log(userAge);
    if (!userGender) {
        setErrorMessage('Kérlek, add meg a nemed!');
        return;
    }

    if(!userAge) {
        setErrorMessage('Kérlek, add meg az életkorod!');
        return;
    }

    if (userAge < 13) {
        setErrorMessage('Az oldalon 13 éven alatti felhasználók nem beszélgethetnek!');
        return;
    }
    
    if (!document.getElementById('userName').value) {
        setErrorMessage('Kérlek add meg a Facebook azonosítód!');
        return;
    }


    if (userAge > 150) {
        setErrorMessage('Kérlek, add meg a valódi életkorod!');
        return;
    }

    if (!partMinAge) {
        setErrorMessage('Kérlek, határozd meg partnered minimum élterkorát!');
        return;
    }

    if (!partMaxAge) {
        setErrorMessage('Kérlek, határzod meg partnered maximum életkorát!');
        return;
    }
    if (partMinAge < 13 || partMaxAge > 150
        || partMaxAge < partMinAge) {
        setErrorMessage('Kérlek, megfelelően add meg\npartnered minumum és maximum életkorát!');
        return;
    }
    if (partMaxAge-partMinAge < 5) {
        setErrorMessage('A partnered életkorának meghatároló mezőinek\nértékei között, minimum 5 év eltérésnek kell lennie!');
        return;
    }

    

    userName = 'https://www.fb.com/'.concat(document.getElementById('userName').value);
    
    if (userTopics) {
        
        const socket = new SockJS('/chat');
        stompClient = Stomp.over(socket);
        login.classList.add('hide');
        stompClient.connect({}, onConnected, onError);
    }

}

const onConnected = () => {
    console.log("connected");
    userId = crypto.randomUUID();

    stompClient.subscribe("/user/" + userId + "/messages", onMessageReceived);

    const newUser = {
                id: userId,
                firstName: userName,
                age: userAge,
                partnerMinAge: partMinAge,
                partnerMaxAge: partMaxAge,
                gender: userGender,
                partnerGender : partGender,
                topics: userTopics,
            }
    
    stompClient.send("/app/register", {}, JSON.stringify(newUser))
    pending.classList.remove('hide');
}

const onError = () => {
    setErrorMessage('Kapcsolati hiba! Kérlek frissítsd az oldalt!😔');
    !document.getElementById('reveal-button').classList.contains('hide') && revealButton.classList.add('hide');
    document.getElementById('message-input').disabled = true;
}

const sendMessage = (event) => {
    
    event.preventDefault();

    const messageInput = document.getElementById('message-input');
    const messageContent = messageInput.value.trim();

    if (messageContent && stompClient) {
        const chatMessage = {
            type: 'CHAT',
            senderId: userId,
            senderGender: userGender,
            content: messageContent,
            time: moment().locale('hu').calendar()
        }
        stompClient.send("/app/message", {}, JSON.stringify(chatMessage));


        //ezt az egészet ki kell rakni functionba
        let messageDiv = document.createElement('div');
        messageDiv.classList.add('my-message');
        messageDiv.textContent = messageContent;
        messageDiv.style.backgroundColor = getGenderColor(userGender);
        let outerMessageDiv = document.createElement('div');
        outerMessageDiv.classList.add('message');
        outerMessageDiv.appendChild(messageDiv);
        outerMessageDiv.title = moment().locale('hu').calendar();
        let chatWindow = document.getElementById('chat-window');
        chatWindow.appendChild(outerMessageDiv);
        chatWindow.scrollTop = chatWindow.scrollHeight;
        messageInput.value = '';

    }
}

const requestReveal = () => {

    if (stompClient) {

        const requestMessage = {
            type: 'REVEAL_REQUEST',
            senderId: userId,
            senderGender: userGender,
            content: "",
            time: moment().locale('hu').calendar()
        }
        stompClient.send("/app/message", {}, JSON.stringify(requestMessage));
        document.getElementById('reveal-button').classList.add('hide');
    }
}

const onMessageReceived = (payload) => {
    const message = JSON.parse(payload.body);

    if (message.type === 'CONNECT') {

        pending.classList.add('hide');
        chatPage.classList.remove('hide'); //itt kell létrehozni, vagy const
        document.getElementById('reveal-button').classList.remove('hide');

        let firstLetters = document.querySelectorAll('#bar > a > span')

        for (let i = 0; i < firstLetters.length; i++) {
            firstLetters[i].style.color = getGenderColor(message.senderGender);
        }

        if (message.content === 'NONE') {
            return;
        }

        let topicDiv = document.createElement("div");
        topicDiv.textContent = "Téma: " + formatTopic(message.content);
        document.getElementById('bar').appendChild(topicDiv);
        return;
    }

    if (message.type === 'DISCONNECT') {
        const messageInput = document.getElementById('message-input');

        messageInput.value = '';
        messageInput.disabled = true;
        
        !document.getElementById('reveal-button').classList.contains('hide') && revealButton.classList.add('hide');

        setErrorMessage('A csevegőtársad elhagyta a beszélgetést😔');
        return;
    }

    if (message.type === 'CHAT') {

        //ezt az egészet ki kell rakni funcitonba
        let messageDiv = document.createElement('div');
        messageDiv.classList.add('mate-message'); // egyenlő legyen nem add
        messageDiv.textContent = message.content;
        messageDiv.style.backgroundColor = getGenderColor(message.senderGender);
        let outerMessageDiv = document.createElement('div');
        outerMessageDiv.classList.add('message');
        outerMessageDiv.appendChild(messageDiv);
        outerMessageDiv.title = message.time;
        let chatWindow = document.getElementById('chat-window');
        chatWindow.appendChild(outerMessageDiv);
        chatWindow.scrollTop = chatWindow.scrollHeight;
        return;
    }

    if (message.type === 'REVEAL_REQUEST' && reveal.isAvailable()) {

        document.getElementById('reveal-button').classList.add('hide');
        document.getElementById('request-window').classList.remove('hide');
    }

    if (message.type === 'REVEAL_REFUSED') {
        console.log('REVEAL_REFUSED!');
        setErrorMessage('REVEAL visszautasítva! 😟')
        document.getElementById('reveal-button').classList.remove('hide');
    }

    if (message.type === 'REVEAL') {

        if (reveal.isAvailable()) {
            sendRevealMessage();
        }

        let chatMateData = JSON.parse(message.content);
        document.getElementById('reveal-age').innerText = 'Kor: ' + chatMateData.age;
        const socialUrl = document.createElement('a');
        socialUrl.href = chatMateData.firstName;
        socialUrl.target = "_blank";
        socialUrl.innerText = "Facebook";
        document.getElementById('reveal-social').innerText = 'Social profil: ';
        document.getElementById('reveal-social').appendChild(socialUrl);

        let formattedTopics = "";
        for (var i = 0; i < chatMateData.topics.length; i++) {
            if (i != 0) {
                formattedTopics += ', ';
            }
            formattedTopics += formatTopic(chatMateData.topics[i]);
        }
        if (formattedTopics != "") {
            document.getElementById('reveal-topics').innerText = 'Témák: ' + formattedTopics;
        }
        let revealDataDiv = document.getElementById('reveal-data');
        revealDataDiv.style.backgroundColor = getGenderColor(message.senderGender);
        revealDataDiv.classList.remove('hide');
    }
}

const sendRevealMessage = () => {

    const userData = {
        firstName: userName,
        age: userAge,
        topics: userTopics,
    }
    
    const revealMessage = {
        type: 'REVEAL',
        senderId: userId,
        senderGender: userGender,
        content: JSON.stringify(userData),
        time: moment().locale('hu').calendar()
    }

    stompClient.send("/app/message", {}, JSON.stringify(revealMessage));
    document.getElementById('request-window').classList.add('hide');
    reveal.setFalse();
}

const refuseReveal = () => {
    document.getElementById('reveal-button').classList.remove('hide');

    let revealMessage = {
        type: 'REVEAL_REFUSED',
        senderId: userId,
        senderGender: userGender,
        content: "",
        time: moment().locale('hu').calendar()
    }

    stompClient.send("/app/message", {}, JSON.stringify(revealMessage));
    document.getElementById('request-window').classList.add('hide');
}

function getTopicsFromForm() {
    userTopics = [];
    let elements = document.querySelectorAll('input[name="topic"]');
    for (var i = 0; i < elements.length; i++) {

        if (elements[i].checked) {
            userTopics.push(elements[i].value);
        }
    }
    return userTopics;
}

function formatTopic(topic) {

    switch(topic) {
        case 'SPORT':
            return 'Sport';
        case 'GAMING':
            return 'Játékok';
        case 'TRAVELING':
            return 'Utazás';
        case 'FOOD':
            return 'Ételek';
        case 'STUDIES':
            return 'Tanulás';
        case 'CODING':
            return 'Kódolás';
        case 'CARS':
            return 'Autók';
        case 'MOVIES':
            return 'Filmek';
        case 'MUSIC':
            return 'Zenék';
        default: 
            return ""; // in case of error
    }
}

function getGenderColor(gender) {

    if(gender === 'FEMALE') {
        return 'orchid';
    } else if(gender === 'MALE') {
        return 'cornflowerblue';
    }
    return 'yellow'; // in case of error
}

loginForm.addEventListener('submit', connect, true)

const revealButton = document.getElementById('reveal-button');
revealButton.addEventListener('click', requestReveal);

const revealAgreeButton = document.getElementById('reveal-agree-button');
revealAgreeButton.addEventListener('click', sendRevealMessage);

const revealRejectButton = document.getElementById('reveal-refuse-button');
revealRejectButton.addEventListener('click', refuseReveal);

const messageInput = document.getElementById('message-input');
messageInput.addEventListener('keypress', function(event) {
    if(event.key === 'Enter') {
        sendMessage(event);
    }
});

const inputHandler = function() {
    const errorText = document.getElementById('error-text');
    while (errorText.firstChild) {
        errorText.removeChild(errorText.firstChild);
    }
}

function setErrorMessage(errorMessage) {
    inputHandler();
    const errorText = document.getElementById('error-text');
    const titleElement = document.createElement('p');
    titleElement.innerText = errorMessage;
    errorText.appendChild(titleElement);
}
document.querySelector('*').addEventListener('input', inputHandler);
document.querySelector('*').addEventListener('propertychange', inputHandler);
document.querySelectorAll('button').forEach(button => {
    button.addEventListener('click', inputHandler)
});