import tensorflow as tf

cluster=tf.train.ClusterSpec({"worker":["10.0.1.8:2222", "10.0.1.9:2222", "10.0.1.10:2222"]})

# the index should be adjusted for different tasks
server=tf.train.Server(cluster, job_name="worker", task_index=0)

matrix = tf.Variable(tf.random_normal([9000, 9000], stddev=0.35, name="matrix"))
init = tf.initialize_all_variables()

with tf.device("/job:worker/task:0"):
    sub_row1=tf.gather(matrix, range(0,3000))
    r1=tf.matmul(sub_row1, matrix)

with tf.device("/job:worker/task:1"):
    sub_row2=tf.gather(matrix, range(3000,6000))
    r2=tf.matmul(sub_row2, matrix)

with tf.device("/job:worker/task:2"):
    sub_row3=tf.gather(matrix, range(6000,9000))
    r3=tf.matmul(sub_row3, matrix)

with tf.device("/job:worker/task:0"):
    all_matrix = tf.concat(0, [r1, r2, r3])

with tf.Session("grpc://localhost:2222") as sess:
    sess.run(init)
    output = sess.run(all_matrix)

output.shape

