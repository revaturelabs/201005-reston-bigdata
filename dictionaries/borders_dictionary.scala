// This dictionary takes a country name and gives back a list of the countries which border it.


val borders_dictionary = Map[String, List[String]](
    "Andorra" -> List(
        "France",
        "Spain"
    ),
    "United Arab Emirates" -> List(
        "Oman",
        "Saudi Arabia"
    ),
    "Afgahnastan" -> List(
        "China",
        "Iran",
        "Pakistan",
        "Tajikastan",
        "Turkmenistan",
        "Uzbekistan"
    ),
    "Antigua and Barbuda" -> List(),
    "Anguilla" -> List(),
    "Albania" -> List(
        "Greece",
        "Montenegro",
        "Macedonia",
        "Serbia"
    ),
    "Armenia" -> List(
        "Azerbaijan",
        "Gerogia",
        "Iran",
        "Turkey"
    ),
    "Angola" -> List(
        "Congo",
        "Democratic Repbulic of Congo",
        "Nambia",
        "Zambia"
    ),
    "Antarctica" -> List(),
    "Argentina" -> List(
        "Bolivia",
        "Brazil",
        "Chile",
        "Paraguay",
        "Uruguay"
    ),
    "American Samoa" -> List(),
    "Austria" -> List(
        "Czechia",
        "Germany",
        "Hungary",
        "Italy",
        "Lietchenstien",
        "Slovakia",
        "Slovenia",
        "Switzerland"
    ),
    "Australia" -> List(),
    "Aruba" -> List(),
    "Aland Islands" -> List(),
    "Azerbaijan" -> List(
        "Armenia",
        "Georgia",
        "Iran",
        "Russia",
        "Turkey"
    ),
    "Bosnia and Herzegovina" -> List(
        "Croatia",
        "Montenegro",
        "Serbia"
    ),
    "Barbados" -> List(),
    "Bangladesh" -> List(
        "India",
        "Myanmar"
    ),
    "Belgium" -> List(
        "France",
        "Germany",
        "Luxembourg",
        "Netherlands"
    ),
    "Burkina Faso" -> List(
        "Benin",
        "Cote d'Ivoire",
        "Ghana",
        "Mali",
        "Niger",
        "Togo"
    ),
    "Bulgaria" -> List(
        "Greece",
        "Macedonia",
        "Romania",
        "Serbia",
        "Turkey"
    ),
    "Bahrain" -> List(),
    "Burundi" -> List(
        "Congo",
        "Rwanda",
        "Tanzania"
    ),
    "Benin" -> List(
        "Burkina Faso",
        "Niger",
        "Nigeria",
        "Togo"
    ),
    "Saint Bathelemy" -> List(),
    "Bermuda" -> List(),
    "Brunei Darussalam" -> List(
        "Malaysia"
    ),
    "Bolivia" -> List(
        "Argentina",
        "Brazil",
        "Chile",
        "Paraguay",
        "Peru"
    ),
    "Bonaire, Sint Eustatius and Saba" -> List(),
    "Brazil" -> List(
        "Argentina",
        "Bolivia",
        "Colombia",
        "French Guiana",
        "Guyana",
        "Paraguay",
        "Peru",
        "Suriname",
        "Uruguay",
        "Venezuela"
    ),
    "Bahamas" -> List(),
    "Bhutan" -> List(
        "China",
        "India"
    ),
    "Bouvet Island" -> List(),
    "Botswana" -> List(
        "Namibia",
        "South Africa",
        "Zambia",
        "Zimbabwe"
    ),
    "Belarus" -> List(
        "Latvia",
        "Lithuania",
        "Poland",
        "Russia",
        "Ukraine"
    ),
    "Belize" -> List(
        "Guatemala",
        "Mexico"
    ),
    "Canada" -> List(
        "United States"
    ),
    "Cocos (Keeling) Islands" -> List(),
    "Democratic Republic of Congo" -> List(
        "Angola",
        "Burundi",
        "Central African Republic",
        "Congo",
        "Rwanda",
        "South Sudan",
        "Tanzania",
        "Uganda",
        "Zambia"
    ),
    "Central African Republic" -> List(
        "Cameroon",
        "Chad",
        "Congo",
        "Congo",
        "South Sudan",
        "Sudan"
    ),
    "Congo" -> List(
        "Angola",
        "Cameroon",
        "Central African Republic",
        "Democratic Republic of Congo",
        "Gabon"
    ),
    "Switzerland" -> List(
        "Austria",
        "France",
        "Germany",
        "Italy",
        "Liechtenstein"
    ),
    "Cote d'Ivoire" -> List(
        "Burkina Faso",
        "Ghana",
        "Guinea",
        "Liberia",
        "Mali"
    ),
    "Cook Islands" -> List(),
    "Chile" -> List(
        "Argentina",
        "Bolivia",
        "Peru"
    ),
    "Cameroon" -> List(
        "Central African Republic",
        "Chad",
        "Congo",
        "Equatorial Guinea",
        "Gabon",
        "Nigeria"
    ),
    "China" -> List(
        "Afghanistan",
        "Bhutan",
        "Hong Kong",
        "India",
        "Kazakhstan",
        "North Korea",
        "Kyrgyzstan",
        "Laos",
        "Macao",
        "Mongolia",
        "Myanmar",
        "Nepal",
        "Pakistan",
        "Russia",
        "Tajikistan",
        "Vietnam"
    ),
    "Colombia" -> List(
        "Brazil",
        "Ecuador",
        "Panama",
        "Peru",
        "Venezuela"
    ),
    "Costa Rica" -> List(
        "Nicaragua",
        "Panama"
    ),
    "Cuba" -> List(),
    "Cabo Verde" -> List(),
    "Curcacao" -> List(),
    "Christmas Island" -> List(),
    "Czechia" -> List(
        "Austria",
        "Germany",
        "Poland",
        "Slovakia"
    ),
    "Germany" -> List(
        "Austria",
        "Belgium",
        "Czechia",
        "Denmark",
        "France",
        "Luxembourg",
        "Netherlands",
        "Poland",
        "Switzerland"
    ),
    "Djibouti" -> List(
        "Eritrea",
        "Ethiopia",
        "Somalia"
    ),
    "Denmark" -> List(
        "Germany"
    ),
    "Dominica" -> List(),
    "Dominican Republic" -> List(
        "Haiti"
    ),
    "Algeria" -> List(
        "Libya",
        "Mali",
        "Mauritania",
        "Morocco",
        "Niger",
        "Tunisia",
        "Western Sahara"
    ),
    "Ecuador" -> List(
        "Colombia",
        "Peru"
    ),
    "Estonia" -> List(
        "Latvia",
        "Russia"
    ),
    "Egypt" -> List(
        "Israel",
        "Libya",
        "Palestine",
        "Sudan"
    ),
    "Western Sahara" -> List(
        "Algeria",
        "Mauritania",
        "Morocco"
    ),
    "Eritrea" -> List(
        "Djibouti",
        "Ethiopia",
        "Sudan"
    ),
    "Spain" -> List(
        "Andorra",
        "France",
        "Gibraltar",
        "Morocco",
        "Portugal"
    ),
    "Ethiopia" -> List(
        "Djibouti",
        "Eritrea",
        "Kenya",
        "Somalia",
        "South Sudan",
        "Sudan"
    ),
    "Finland" -> List(
        "Norway",
        "Russia",
        "Sweden"
    ),
    "Fiji" -> List(),
    "Falkland Islands" -> List(),
    "Federated States of Micronesia" -> List(),
    "Faroe Island" -> List(),
    "France" -> List(
        "Andorra",
        "Belgium",
        "Germany",
        "Italy",
        "Luxembourg",
        "Monaco",
        "Spain",
        "Switzerland"
    ),
    "Gabon" -> List(
        "Cameroon",
        "Congo",
        "Equatorial Guinea"
    ),
    "United Kingdom" -> List(
        "France",
        "Ireland"
    ),
    "Grenada" -> List(),
    "Georgia" -> List(
        "Armenia",
        "Azerbaijan",
        "Russia",
        "Turkey"
    ),
    "French Guiana" -> List(
        "Brazil",
        "Suriname"
    ),
    "Guernsey" -> List(),
    "Ghana" -> List(
        "Burkina Faso",
        "Cote d’Ivoire",
        "Togo"
    ),
    "Gibraltar" -> List(
        "Spain"
    ),
    "Greenland" -> List(),
    "Gambia" -> List(
        "Senegal"
    ),
    "Guinea" -> List(
        "Cote d’Ivoire",
        "Guinea-Bissau",
        "Liberia",
        "Mali",
        "Senegal",
        "Sierra Leone"
    ),
    "Guadeloupe" -> List(),
    "Equatorial Guinea" -> List(
        "Cameroon",
        "Gabon"
    ),
    "Greece" -> List(
        "Albania",
        "Bulgaria",
        "Macedonia",
        "Turkey"
    ),
    "Guatemala" -> List(
        "Belize",
        "El Salvador",
        "Honduras",
        "Mexico"
    ),
    "Guam" -> List(),
    "Guinea-Bissau" -> List(
        "Guinea",
        "Senegal"
    ),
    "Guyana" -> List(
        "Brazil",
        "Suriname",
        "Venezuela"
    ),
    "Hong Kong" -> List(
        "China"
    ),
    "Honduras" -> List(
        "El Salvador",
        "Guatemala",
        "Nicaragua"
    ),
    "Croatia" -> List(
        "Bosnia and Herzegovina",
        "Hungary",
        "Montenegro",
        "Serbia",
        "Slovenia"
    ),
    "Haiti" -> List(
        "Dominican Republic"
    ),
    "Hungary" -> List(
        "Austria",
        "Croatia",
        "Romania",
        "Serbia",
        "Slovakia",
        "Slovenia",
        "Ukraine"
    ),
    "Indonesia" -> List(
        "Malaysia",
        "Papua New Guinea",
        "Timor-Leste"
    ),
    "Ireland" -> List(
        "United Kingdom"
    ),
    "Israel" -> List(
        "Egypt",
        "Jordan",
        "Lebanon",
        "Palestine",
        "Syria"
    ),
    "Isle of Man" -> List(),
    "India" -> List(
        "Bangladesh",
        "Bhutan",
        "China",
        "Myanmar",
        "Nepal",
        "Pakistan"
    ),
    "British Indian Ocean Territory" -> List(),
    "Iraq" -> List(
        "Iran",
        "Jordan",
        "Kuwait",
        "Saudi Arabia",
        "Syria",
        "Turkey"
    ),
    "Iran" -> List(
        "Afghanistan",
        "Armenia",
        "Azerbaijan",
        "Iraq",
        "Pakistan",
        "Turkey",
        "Turkmenistan"
    ),
    "Iceland" -> List(),
    "Italy" -> List(
        "Austria",
        "France",
        "San Marino",
        "Slovenia",
        "Switzerland",
        "Holy See"
    ),
    "Jersey" -> List(),
    "Jamaica" -> List(),
    "Jordan" -> List(
        "Iraq",
        "Israel",
        "Palestine",
        "Saudi Arabia",
        "Syria"
    ),
    "Japan" -> List(),
    "Kenya" -> List(
        "Ethiopia",
        "Somalia",
        "South Sudan",
        "Tanzania",
        "Uganda"
    ),
    "Kyrgyzstan" -> List(
        "China",
        "Kazakhstan",
        "Tajikistan",
        "Uzbekistan"
    ),
    "Cambodia" -> List(
        "Laos",
        "Thailand",
        "Vietnam"
    ),
    "Kiribati" -> List(),
    "Comorros" -> List(),
    "Saint Kitts and Nevis" -> List(),
    "North Korea" -> List(
        "China",
        "South Korea",
        "Russia"
    ),
    "South Korea" -> List(
        "North Korea"
    ),
    "Kuwait" -> List(
        "Iraq",
        "Saudi Arabia"
    ),
    "Cayman Island" -> List(),
    "Kazakhstan" -> List(
        "China",
        "Kyrgyzstan",
        "Russia",
        "Turkmenistan",
        "Uzbekistan"
    ),
    "Laos" -> List(
        "China",
        "Cambodia",
        "Myanmar",
        "Thailand",
        "Vietnam"
    ),
    "Lebanon" -> List(
        "Israel",
        "Syria"
    ),
    "Saint Lucia" -> List(),
    "Liechtenstien" -> List(
        "Austria",
        "Switzerland"
    ),
    "Sri Lanka" -> List(),
    "Liberia" -> List(
        "Cote d’Ivoire",
        "Guinea",
        "Sierra Leone"
    ),
    "Lesotho" -> List(
        "South Africa"
    ),
    "Lithuania" -> List(
        "Belarus",
        "Latvia",
        "Poland",
        "Russia"
    ),
    "Luxembourg" -> List(
        "Belarus",
        "Latvia",
        "Poland",
        "Russia"
    ),
    "Latvia" -> List(
        "Belarus",
        "Estonia",
        "Lithuania",
        "Russia"
    ),
    "Libya" -> List(
        "Algeria",
        "Chad",
        "Egypt",
        "Niger",
        "Sudan",
        "Tunisia"
    ),
    "Morocco" -> List(
        "Algeria",
        "Spain",
        "Western Sahara"
    ),
    "Monaco" -> List(
        "France"
    ),
    "Moldova" -> List(
        "Romania",
        "Ukraine"
    ),
    "Montenegro" -> List(
        "Albania",
        "Bosnia and Herzegovina",
        "Croatia",
        "Serbia"
    ),
    "Madagascar" -> List(),
    "Marshall Islands" -> List(),
    "Macedonia" -> List(
        "Albania",
        "Bulgaria",
        "Greece",
        "Serbia"
    ),
    "Mali" -> List(
        "Algeria",
        "Burkina Faso",
        "Cote d’Ivoire",
        "Guinea",
        "Mauritania",
        "Niger",
        "Senegal"
    ),
    "Myanmar" -> List(
        "Bangladesh",
        "China",
        "India",
        "Laos",
        "Thailand"
    ),
    "Mongolia" -> List(
        "China",
        "Russia"
    ),
    "Macao" -> List(
        "China"
    ),
    "Northern Mariana Islands" -> List(),
    "Martinique" -> List(),
    "Mauritania" -> List(
        "Algeria",
        "Mali",
        "Senegal",
        "Western Sahara"
    ),
    "Montserrat" -> List(),
    "Malta" -> List(),
    "Mauritius" -> List(),
    "Maldives" -> List(),
    "Malawi" -> List(
        "Mozambique",
        "Tanzania",
        "Zambia"
    ),
    "Mexico" -> List(
        "Belize",
        "Guatemala",
        "United States"
    ),
    "Malaysia" -> List(
        "Brunei Darussalam",
        "Indonesia",
        "Thailand"
    ),
    "Mozambique" -> List(
        "Malawi",
        "Eswatini",
        "South Africa",
        "Tanzania",
        "Zambia",
        "Zimbabwe"
    ),
    "Namibia" -> List(
        "Angola",
        "Botswana",
        "South Africa",
        "Zambia"
    ),
    "New Caldonia" -> List(),
    "Niger" -> List(
        "Algeria",
        "Benin",
        "Burkina Faso",
        "Chad",
        "Libya",
        "Mali",
        "Nigeria"
    ),
    "Norfolk Island" -> List(),
    "Nigeria" -> List(
        "Benin",
        "Cameroon",
        "Chad",
        "Niger"
    ),
    "Nicaragua" -> List(
        "Costa Rica",
        "Honduras"
    ),
    "Netherlands" -> List(
        "Belgium",
        "Germany"
    ),
    "Norway" -> List(
        "Finland",
        "Russia",
        "Sweden"
    ),
    "Nepal" -> List(
        "China",
        "India"
    ),
    "Nauru" -> List(),
    "Niue" -> List(),
    "New Zealand" -> List(),
    "Oman" -> List(
        "United Arab Emirates",
        "Saudi Arabia",
        "Yemen"
    ),
    "Panama" -> List(
        "Colombia",
        "Costa Rica"
    ),
    "Peru" -> List(
        "Bolivia",
        "Brazil",
        "Chile",
        "Colombia",
        "Ecuador"
    ),
    "French Polynesia" -> List(),
    "Papua New Guinea" -> List(
        "Indonesia"
    ),
    "Philippines" -> List(),
    "Pakistan" -> List(
        "Afghanistan",
        "China",
        "India",
        "Iran"
    ),
    "Poland" -> List(
        "Belarus",
        "Czechia",
        "Germany",
        "Lithuania",
        "Russia",
        "Slovakia",
        "Ukraine"
    ),
    "Saint Pierre and Miquelon" -> List(),
    "Pitcairn" -> List(),
    "Puerto Rico" -> List(),
    "Palestine" -> List(
        "Egypt",
        "Israel",
        "Jordan"
    ),
    "Portugal" -> List(
        "Spain"
    ),
    "Palau" -> List(),
    "Paraguay" -> List(
        "Argentina",
        "Bolivia",
        "Brazil"
    ),
    "Qatar" -> List(
        "Saudi Arabia"
    ),
    "Romania" -> List(
        "Bulgaria",
        "Hungary",
        "Moldova",
        "Serbia",
        "Ukraine"
    ),
    "Serbia" -> List(
        "Albania",
        "Bosnia and Herzegovina",
        "Bulgaria",
        "Croatia",
        "Hungary",
        "Montenegro",
        "Macedonia",
        "Romania"
    ),
    "Russia" -> List(
        "Azerbaijan",
        "Belarus",
        "China",
        "Estonia",
        "Finland",
        "Georgia",
        "Kazakhstan",
        "North Korea",
        "Latvia",
        "Lithuania",
        "Mongolia",
        "Norway",
        "Poland",
        "Ukraine"
    ),
    "Rwanda" -> List(
        "Burundi",
        "Congo",
        "Tanzania",
        "Uganda"
    ),
    "Saudi Arabia" -> List(
        "Iraq",
        "Jordan",
        "Kuwait",
        "Oman",
        "Qatar",
        "United Arab Emirates",
        "Yemen"
    ),
    "Solomon Islands" -> List(),
    "Seychelles" -> List(),
    "Sudan" -> List(
        "Central African Republic",
        "Chad",
        "Egypt",
        "Ethiopia",
        "Eritrea",
        "Libya",
        "South Sudan"
    ),
    "Sweden" -> List(
        "Finland",
        "Norway"
    ),
    "Singapore" -> List(),
    "Saint Helena" -> List(),
    "Slovenia" -> List(
        "Austria",
        "Croatia",
        "Hungary",
        "Italy"
    ),
    "Svalbard and Jan Mayen" -> List(),
    "Slovakia" -> List(
        "Austria",
        "Czechia",
        "Hungary",
        "Poland",
        "Ukraine"
    ),
    "Sierra Leone" -> List(
        "Guinea",
        "Liberia"
    ),
    "San Marino" -> List(
        "Italy"
    ),
    "Senegal" -> List(
        "Gambia",
        "Guinea",
        "Guinea-Bissau",
        "Mali",
        "Mauritania"
    ),
    "Somalia" -> List(
        "Djibouti",
        "Ethiopia",
        "Kenya"
    ),
    "Suriname" -> List(
        "Brazil",
        "French Guiana",
        "Guyana"
    ),
    "South Sudan" -> List(
        "Central African Republic",
        "Congo",
        "Ethiopia",
        "Kenya",
        "Sudan",
        "Uganda",
    ),
    "Sao Tome and Principe" -> List(),
    "El Salvador" -> List(
        "Guatemala",
        "Honduras"
    ),
    "Sint Maartin" -> List(),
    "Syria" -> List(
        "Iraq",
        "Israel",
        "Jordan",
        "Lebanon",
        "Turkey"
    ),
    "Eswatini" -> List(
        "Mozambique",
        "South Africa"
    ),
    "Turks and Caicos Islands" -> List(),
    "Chad" -> List(
        "Cameroon",
        "Central African Republic",
        "Libya",
        "Niger",
        "Nigeria",
        "Sudan"
    ),
    "Togo" -> List(
        "Benin",
        "Burkina Faso",
        "Ghana"
    ),
    "Thailand" -> List(
        "Cambodia",
        "Laos",
        "Malaysia",
        "Myanmar"
    ),
    "Tajikistan" -> List(
        "Afghanistan",
        "China",
        "Kyrgyzstan",
        "Uzbekistan"
    ),
    "Tokelau" -> List(),
    "Timor-Leste" -> List(),
    "Turkmenistan" -> List(
        "Indonesia",
        "Afghanistan",
        "Iran",
        "Kazakhstan",
        "Uzbekistan"
    ),
    "Tunisia" -> List(
        "Algeria",
        "Libya"
    ),
    "Tonga" -> List(),
    "Turkey" -> List(
        "Armenia",
        "Azerbaijan",
        "Bulgaria",
        "Georgia",
        "Greece",
        "Iran",
        "Iraq",
        "Syria"
    ),
    "Trinidad and Tobago" -> List(),
    "Tuvalu" -> List(),
    "Taiwan" -> List(),
    "Tanzania" -> List(
        "Burundi",
        "Congo",
        "Kenya",
        "Malawi",
        "Mozambique",
        "Rwanda",
        "Uganda",
        "Zambia"
    ),
    "Ukraine" -> List(
        "Belarus",
        "Hungary",
        "Moldova",
        "Poland",
        "Romania",
        "Russia",
        "Slovakia"
    ),
    "Uganda" -> List(
        "Congo",
        "Kenya",
        "Rwanda",
        "South Sudan",
        "Tanzania"
    ),
    "United States" -> List(
        "Canada",
        "Mexico"
    ),
    "Uruguay" -> List(
        "Argentina",
        "Brazil"
    ),
    "Uzbekistan" -> List(
        "Afghanistan",
        "Kazakhstan",
        "Kyrgyzstan",
        "Tajikistan",
        "Turkmenistan"
    ),
    "Holy See" -> List(
        "Italy"
    ),
    "Venezuela" -> List(
        "Brazil",
        "Colombia",
        "Guyana"
    ),
    "Virgin Islands" -> List(),
    "Vietnam" -> List(
        "Cambodia",
        "China",
        "Laos"
    ),
    "Vanuatu" -> List(),
    "Samoa" -> List(),
    "Yemen" -> List(
        "Oman",
        "Saudi Arabia"
    ),
    "South Africa" -> List(
        "Botswana",
        "Lesotho",
        "Mozambique",
        "Namibia",
        "Eswatini",
        "Zimbabwe"
    ),
    "Zambia" -> List(
        "Angola",
        "Botswana",
        "Congo",
        "Malawi",
        "Mozambique",
        "Namibia",
        "Tanzania",
        "Zimbabwe"
    ),
    "Zimbabwe" -> List(
        "Botswana",
        "Mozambique",
        "South Africa",
        "Zambia"
    )
)
