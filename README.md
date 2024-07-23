<a id="readme-top"></a>

<!-- PROJECT SHIELDS -->
[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![MIT License][license-shield]][license-url]
[![LinkedIn][linkedin-shield]][linkedin-url]

<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/vectorete/server-multiclient-drones">
    <img src="images/logo.png" alt="Logo" width="30%">
  </a>

<h3 align="center">Drone Monitoring System</h3>

  <p align="center">
    A client-server architecture drone monitoring system to manage and simulate large-scale drone operations.
    <br />
    <a href="https://github.com/vectorete/server-multiclient-drones"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://github.com/vectorete/server-multiclient-drones">View Demo</a>
    ·
    <a href="https://github.com/vectorete/server-multiclient-drones/issues/new?labels=bug&template=bug-report---.md">Report Bug</a>
    ·
    <a href="https://github.com/vectorete/server-multiclient-drones/issues/new?labels=enhancement&template=feature-request---.md">Request Feature</a>
  </p>
</div>

<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">🔎 About The Project</a>
      <ul>
        <li><a href="#features">✨ Features</a></li>
        <li><a href="#built-with">🔨 Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">🚀 Getting Started</a>
      <ul>
        <li><a href="#prerequisites">📝 Prerequisites</a></li>
        <li>
          <a href="#installation">📥 Installation</a>
          <ul>
            <li><a href="#server-setup">🖥 Server Setup</a></li>
            <li><a href="#client-setup">👨🏻‍💼 Client Setup</a></li>
          </ul>
        </li>
      </ul>
    </li>
    <li>
      <a href="#usage">🧩 Usage</a>
      <ul>
        <li><a href="#testing-locally">🧪 Testing Locally</a></li>
        <li>
          <a href="#demos">📽️ Demos</a>
          <ul>
            <li><a href="#demo-with-60000-clients">🥇 Demo with 60,000 clients</a></li>
            <li><a href="#demo-showcasing-the-interface">🥈 Demo showcasing the interface</a></li>
          </ul>
        </li>
      </ul>
    </li>
    <li><a href="#contributing">🤝 Contributing</a></li>
    <li><a href="#license">©️ License</a></li>
    <li><a href="#contact">☎️ Contact</a></li>
  </ol>
</details>

<!-- ABOUT THE PROJECT -->
## 🔎 About The Project <a id="about-the-project"></a>

<div align="center">
  <a href="https://github.com/vectorete/server-multiclient-drones">
    <img src="images/projectimg.png" alt="Project" width="50%">
  </a>
</div>

The Drone Monitoring System is a project that simulates a drone monitoring system using a client-server architecture. The server manages multiple drone clients, processes their positions, and forwards position data to other drones. The system does not prevent collisions but efficiently manages a large number of clients for testing purposes.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### ✨ Features <a id="features"></a>

- **Client-Server Architecture**: Uses TCP sockets to establish communication between drones (clients) and the server.
- **Client Simulation**: Clients send position data to the server, which then forwards this data to other clients.
- **Concurrency**: The server handles multiple clients concurrently using threads.
- **Performance Testing**: Designed to simulate a high volume of clients to test server performance.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### 🔨 Built With <a id="built-with"></a>
* [![Java][Java]][Java-url]

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- GETTING STARTED -->
## 🚀 Getting Started <a id="getting-started"></a>

To get a local copy up and running follow these simple steps.

### 📝 Prerequisites <a id="prerrequisites"></a>

* Java Development Kit (JDK): Ensure you have JDK 8 or higher installed.
* Network Configuration: Ensure your network allows communication on the specified ports.

### 📥 Installation <a id="installation"></a>

#### 🖥 Server Setup <a id="server-setup"></a>

1. **Compile the server code**:
    ```sh
    javac Servidor.java
    ```
2. **Run the server**:
    ```sh
    java Servidor.java
    ```

#### 👨🏻‍💼 Client Setup <a id="client-setup"></a>

1. **Compile the client code**:
    ```sh
    javac Clientes.java
    ```
2. **Run the clients**:
    ```sh
    java Clientes.java
    ```

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- USAGE EXAMPLES -->
## 🧩 Usage <a id="usage"></a>

### 🧪 Testing Locally <a id="testing-locally"></a>

When testing locally, be aware of the following:

- **Port Limits on Windows**: Windows may lock up to 16,384 ports for dynamic use. To test with more clients than this limit, you have two options:
  1. **Use Multiple PCs**: Distribute the clients across multiple machines to avoid hitting the port limit.
  2. **Change Port Limits**: Use the following command to view and adjust the dynamic port range on Windows:
     ```sh
     netsh int ipv4 show dynamicportrange tcp
     ```
     To change the dynamic port range, use:
     ```sh
     netsh int ipv4 set dynamicportrange tcp start=xxxxx num=yyyyy
     ```
     Replace `xxxxx` with the starting port number and `yyyyy` with the number of ports.

### 📽️ Demos <a id="demos"></a>

#### 🥇 Demo with 60,000 clients <a id="#demo-with-60000-clients"></a>
[![Demo1](https://img.youtube.com/vi/ryzMgj_m2p8/0.jpg)](https://www.youtube.com/watch?v=ryzMgj_m2p8)

#### 🥈 Demo showcasing the interface <a id="#demo-showcasing-the-interface">
[![Demo2](https://img.youtube.com/vi/8YvBKZ6ilX4/0.jpg)](https://www.youtube.com/watch?v=8YvBKZ6ilX4)

_For more examples, please refer to the [Documentation](https://example.com)_

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- CONTRIBUTING -->
## 🤝 Contributing <a id="#contributing">

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement". Don't forget to give the project a star! Thanks again!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- LICENSE -->
## ©️ License <a id="#license">

Distributed under the MIT License. See `LICENSE.txt` for more information.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- CONTACT -->
## ☎️ Contact <a id="#contact">

Victor Kravchuk Vorkevych - victorkravchuk19@gmail.com

Project Link: [https://github.com/vectorete/server-multiclient-drones](https://github.com/vectorete/server-multiclient-drones)

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/vectorete/server-multiclient-drones.svg?style=for-the-badge
[contributors-url]: https://github.com/vectorete/server-multiclient-drones/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/vectorete/server-multiclient-drones.svg?style=for-the-badge
[forks-url]: https://github.com/vectorete/server-multiclient-drones/network/members
[stars-shield]: https://img.shields.io/github/stars/vectorete/server-multiclient-drones.svg?style=for-the-badge
[stars-url]: https://github.com/vectorete/server-multiclient-drones/stargazers
[issues-shield]: https://img.shields.io/github/issues/vectorete/server-multiclient-drones.svg?style=for-the-badge
[issues-url]: https://github.com/vectorete/server-multiclient-drones/issues
[license-shield]: https://img.shields.io/github/license/vectorete/server-multiclient-drones.svg?style=for-the-badge
[license-url]: https://github.com/vectorete/server-multiclient-drones/blob/master/LICENSE.txt
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555
[linkedin-url]: https://linkedin.com/in/vectorete
[product-project]: images/projectimg.png
[Java]:	https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white
[Java-url]: https://java.com
