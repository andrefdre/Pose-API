<a name="readme-top"></a>

[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![MIT License][license-shield]][license-url]

<h1 align="center">
  <!-- <br>
  <a href="https://github.com/andrefdre/Pose-API"><img src="./Images/Logo.svg" alt="Pose-API" width="400"></a>
  <br> -->
    Pose-API
  <br>
</h1>
  <p align="center">
    The Pose-Api mod is a custom Minecraft mod developed using the Minecraft Fabric modding platform. It allows you to retrieve a series of important info regarding your in-game character.
    Its main purpose is to create a communication interface with the game to then generate datasets for Deep-Learning purposes.
    <br />
    <!-- <a href="https://github.com/andrefdre/Pose-API/wiki"><strong>Explore the Wiki »</strong></a> -->
    <!-- <br /> -->
    <br />
    <!-- <a href="https://youtu.be/vULnTanHHmM">View Demo</a> -->
    ·
    <a href="https://github.com/andrefdre/Pose-API/issues">Report Bug</a>
    ·
    <a href="https://github.com/andrefdre/Pose-API/issues">Request Feature</a>
  </p>

<!-- ![screenshot](https://raw.githubusercontent.com/amitmerchant1990/electron-markdownify/master/app/img/markdownify.gif) -->

<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#key-features">Key Features</a>
    </li>
    <li>
      <a href="#how-to-use">How to use</a>
    </li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>

## Key Features

- This mod allows you to retrieve key info from you in game character.
- It is developed using the Minecraft Fabric modding platform.
- It allows you to retrieve 4 key features from your in game character:
  - <b>Player position</b>: coordinates (X,Y,Z) plus its rotation properties (yaw + pitch);
  - <b>Player inventory</b>: Hotbar and remaining inventory;
  - <b>Key inputs</b>: constantly monitor main key inpouts such as WASD, jump or right and left click;
  - <b>Gameplay screenshots</b>: able to take screenshots of the current gameplay.
- All this info is exported using a Rest API placed in localhost:8070.
<p align="right">(<a href="#readme-top">back to top</a>)</p>

## How To Use

#### Pre-requisites

- Java 17
- Any Java IDE, for example Intellij IDEA and Eclipse. You may also use any other code editors, such as Visual Studio Code.
- If you use Virtual Studio Code, we recommend installing the Extension Pack for Java (https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack). To do so, simply launch VS Code Quick Open (Ctrl + P) and run:

```
ext install vscjava.vscode-java-pack
```

#### Installation

To build the project you need to first install the sources of the Minecraft Fabric modding platform. But first if you using VS Code you need to run the following command in the terminal:

```
./gradlew vscode
```

Then you can generate the sources of the project using the following command:

```
./gradlew genSources
```

To build the project you can use the following command:

```
./gradlew build
```

Note that everytime you input a new dependency in the 'build.gradle' file, you should always run the command that generates the sources. That allows you to update the dependencies.


You can find the full instructions [here](https://fabricmc.net/wiki/tutorial:setup).

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- CONTRIBUTING -->
## Contributing

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".
Don't forget to give the project a star! Thanks again!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- LICENSE -->
## License

Distributed under the CCO License. See `LICENSE.txt` for more information.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- CONTACT -->
## Contact

André Cardoso - andref@ua.pt

José Cação - josemaria@ua.pt

Project Link: [Pose-API](https://github.com/andrefdre/Pose-API)

<p align="right">(<a href="#readme-top">back to top</a>)</p>




<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/andrefdre/Pose-API.svg?style=for-the-badge
[contributors-url]: https://github.com/andrefdre/Pose-API/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/andrefdre/Pose-API.svg?style=for-the-badge
[forks-url]: https://github.com/andrefdre/Pose-API/network/members
[stars-shield]: https://img.shields.io/github/stars/andrefdre/Pose-API.svg?style=for-the-badge
[stars-url]: https://github.com/andrefdre/Pose-API/stargazers
[issues-shield]: https://img.shields.io/github/issues/andrefdre/Pose-API.svg?style=for-the-badge
[issues-url]: https://github.com/andrefdre/Pose-API/issues
[license-shield]: https://img.shields.io/github/license/andrefdre/Pose-API.svg?style=for-the-badge
[license-url]: https://github.com/andrefdre/Pose-API/blob/master/LICENSE.txt
[product-screenshot]: Docs/logo.svg
