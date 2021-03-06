package com.cucumber.utils.context.stepdefs.jsch;

import com.cucumber.utils.context.props.ScenarioProps;
import com.google.inject.Inject;
import com.jcraft.jsch.JSchException;
import io.cucumber.guice.ScenarioScoped;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.jtest.utils.clients.jsch.JschClient;
import io.jtest.utils.common.ResourceUtils;
import io.jtest.utils.matcher.ObjectMatcher;

import java.io.IOException;
import java.util.Properties;

@ScenarioScoped
public class JschSteps {

    private JschClient client;

    @Inject
    private ScenarioProps scenarioProps;

    @Given("JSCH connection from properties file \"{}\"")
    public void init(String relFilePath) throws IOException, JSchException {

        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");

        Properties connProps = ResourceUtils.readProps(relFilePath);
        String host = connProps.getProperty("host").trim();
        int port = Integer.parseInt(connProps.getProperty("port", "22").trim());
        String user = connProps.getProperty("user").trim();
        String pwd = connProps.getProperty("password", "").trim();
        String privateKey = connProps.getProperty("privateKey").trim();

        this.client = new JschClient(host, port, user, pwd, privateKey, config);
        this.client.connect();
    }

    @Then("JSCH execute command \"{}\" and check response=\"{}\"")
    public void executeCmd(String cmd, String expected) throws IOException, JSchException {
        String actual = this.client.sendCommand(cmd).trim();
        scenarioProps.putAll(ObjectMatcher.match(null, expected, actual));
    }

    @After("@jsch_cleanup")
    @And("JSCH disconnect")
    public void disconnect() {
        if (this.client != null) {
            this.client.disconnect();
        }
    }
}
