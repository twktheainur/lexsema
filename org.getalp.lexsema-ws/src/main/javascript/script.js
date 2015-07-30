
function manageMouseEvent(wordIndex)
{
    var wordNode = document.getElementById("wsdword" + wordIndex);
    wordNode.style.borderBottom = '1px dashed #aaaaff';
    var senseNode = document.getElementById("wsdsense" + wordIndex);
    senseNode.style.display = "none";
    senseNode.style.color = "blue";
    senseNode.style.position = "fixed";
    senseNode.style.border = "1px solid black";
    senseNode.style.background = "#ddd";
    senseNode.style.padding = "5px";
    wordNode.onmouseover = function(event)
    {
        senseNode.style.display = "block"; 
        senseNode.style.left = event.clientX + "px";
        senseNode.style.top = (event.clientY + 10) + "px";
    };
    wordNode.onmouseout = function(){ senseNode.style.display = "none"; };
}

function requestAnnotatingServer(paragraph, m)
{
    var req = new XMLHttpRequest();
    req.open("POST", "https://localhost:8443/org.getalp.lexsema-ws/htmltextannotator", true);
    req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    req.send("i=" + encodeURIComponent(paragraph.innerHTML) + "&m=" + m);
    req.onreadystatechange = function()
    {
        if (req.readyState == 4 && (req.status == 200 || req.status == 0))
        {
            var res = req.responseXML;
            var content = res.getElementsByTagName("content")[0].childNodes[0].nodeValue;
            var n = res.getElementsByTagName("n")[0].textContent;
            paragraph.innerHTML = content;
            for (i = 0 ; i < n ; i++)
            {
                manageMouseEvent(i + m);
            }
        }
    }
}

function wrap(paragraphs)
{
    var wrapper = document.createElement('div');
    paragraphs[0].parentNode.replaceChild(wrapper, paragraphs[0]);
    wrapper.appendChild(paragraphs[0]);
    for (i = 1 ; i < paragraphs.length ; i++)
    {
        wrapper.appendChild(paragraphs[i]);
    }
    return wrapper;
}

function getSections()
{
    var sections = [];
    var parent = document.getElementById("mw-content-text");
    var currentSection = [];
    for (i = 0 ; i < parent.childElementCount ; i++)
    {
       var child = parent.children.item(i);
       if (child.tagName == "P" || child.tagName == "UL" || child.tagName == "OL")
       {
           currentSection.push(child);
       }
       else
       {
           if (currentSection.length != 0)
           {
              sections.push(wrap(currentSection));
              currentSection = [];
           }
       }
    }
    return sections;
}

var sections = getSections();
for (i = 0 ; i < sections.length ; i++)
{
    requestAnnotatingServer(sections[i], i * 1000);
}


