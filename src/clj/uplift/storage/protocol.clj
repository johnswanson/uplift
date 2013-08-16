(ns uplift.storage.protocol)

(defprotocol Storage
  (add-user [this username password])
  (get-user [this username])
  (change-password [this user new-password]))

(defrecord MemoryStorage [db]
  Storage
  (add-user [_ username password]
    (let [next-id (:next-id @db)
          user {:id next-id
                :username username
                :password password}]
      (swap! db (fn [db]
                  (-> db
                    (assoc :next-id (inc next-id))
                    (assoc-in [:users next-id] user))))
      user))

  (get-user [_ username]
    (->> (map val (:users @db))
      (filter #(= (:username %) username))
      (first)))

  (change-password [_ {id :id} password]
    (swap! db (fn [db]
                (assoc-in db [:users id :password] password)))))
