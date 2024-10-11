FROM gyyx-flink/flink:1.17.1
##FROM gyyx/debian11-jdk11:1.0
# Set the working directory
WORKDIR /data

# Copy the application jar
COPY target/*.jar /data/program.jar

# Set appropriate permissions and combine RUN commands to reduce layers
RUN chmod 644 /data/program.jar
